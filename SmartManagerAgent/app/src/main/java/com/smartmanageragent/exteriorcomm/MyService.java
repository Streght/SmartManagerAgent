package com.smartmanageragent.exteriorcomm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.agent.AgentImpl;
import com.smartmanageragent.smartagent.message.JSONMessage;
import com.smartmanageragent.smartagent.message.MessageQueue;
import com.smartmanageragent.smartagent.message.Serializer;
import com.smartmanageragent.smartagent.message.WaitingQueue;
import com.smartmanageragent.smartagent.timeTable.Activity;
import com.smartmanageragent.smartagent.timeTable.TimeTable;
import com.smartmanageragent.smartagent.timeTable.TimeTableImpl;

import java.io.NotSerializableException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

public class MyService extends Service {

    private final IBinder myBinder = new MyLocalBinder();
    public static String postIp = "POSTIP";
    public static String getMap = "GETMAP";
    private static String serverName = "http://calendar-matcher.spieldy.com/index.php";
    private static MessageQueue<String> receive;
    private static MessageQueue<String> send;
    private static Agent<Date, Float, String> agent;
    private SharedPreferences sharedPreferences = null;
    private static WaitingQueue<String> waitingQueue;

    public static MessageQueue<String> getReceive() {
        return receive;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences("com.smartmanageragent.exteriorcomm", MODE_PRIVATE);

        String receiveSaved = sharedPreferences.getString("receive", "");
        String sendSaved = sharedPreferences.getString("send", "");

        if (!(receiveSaved.equals(""))) {
            receive = (MessageQueue<String>) Serializer.deserialize(receiveSaved);
        } else {
            receive = new MessageQueue<>();
        }
        if (!(sendSaved.equals(""))) {
            send = (MessageQueue<String>) Serializer.deserialize(sendSaved);
        } else {
            send = new MessageQueue<>();
        }

        Timer t = new Timer();
        t.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        try {
                            editor.putString("timeTable", Serializer.serialize(agent.getTimeTable()));
                            editor.putString("receive", Serializer.serialize(receive));
                            editor.putString("send", Serializer.serialize(send));
                            editor.apply();
                        } catch (NotSerializableException e) {
                            e.printStackTrace();
                        }
                    }
                },
                10000,
                30000);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {

        String name = intent.getExtras().getString("username");
        waitingQueue = new WaitingQueue<>(send);
        agent = new AgentImpl<>(name, receive, send);

        String timeTableSaved = sharedPreferences.getString("timeTable", "");

        if (!(timeTableSaved.equals(""))) {

            TimeTable<Date, Float> timeTable = (TimeTable<Date, Float>) Serializer.deserialize(timeTableSaved);
            // TODO uncomment when available
            //agent.setTimeTable(timeTable);
        } else {
            Activity<Float> empty = new Activity<>((float) 1, TimeTableImpl.unPriority, TimeTableImpl.unName);
            Date beg = new Date(0);
            Date end = new Date(10000);

            TimeTable<Date, Float> timeTable = agent.getTimeTable();
            timeTable.addActivity(beg, empty);
            timeTable.addActivity(end, empty);
        }

        new Thread(agent).start();

        Thread threadSender = new Thread() {
            @Override
            public void run() {
                try {
                    while (true) {
                        JSONMessage message2Send = (JSONMessage) send.get();
                        fonctionMagique(message2Send);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        threadSender.start();

        // TODO uncomment when error fixed : java.lang.IllegalMonitorStateException: object not locked by thread before wait()
        /*
        Thread threadWaitinQueue = new Thread() {
            @Override
            public void run () {
                try {
                    while (true) {
                       // TODO Error on this wait()
                        wait(waitingQueue.getWaitingTime());
                        waitingQueue.emptyToMessageQueue();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        threadWaitinQueue.start();
        */

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return myBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public TimeTable<Date, Float> getAgentTimeTable() {
        return agent.getTimeTable();
    }

    public class MyLocalBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }


    private void fonctionMagique(JSONMessage request) {
        Log.d(TAG, "Début Envoi message");
        if (request.getField(JSONMessage.Fields.ACTIVITY).equals("LOCAL")) {
            // TODO : La notification à NICOLAS !!
        } else if (request.getField(JSONMessage.Fields.COMMAND).equals(postIp)) {
            postIP2Server(request);
        } else if (request.getField(JSONMessage.Fields.COMMAND).equals(getMap)) {
            updateMap();
        } else {
            String addresseeListString = request.getField(JSONMessage.Fields.ADDRESSEES);
            List<String> addresseeList = new ArrayList<String>(Arrays.asList(addresseeListString.split(",")));
            if (addresseeList != null) {
                for (String ad : addresseeList) {
                    String ipAd = SingletonRegisterIDIP.getInstance().getIp(ad);
                    JSONMessage newRequest = request;
                    newRequest.setField(JSONMessage.Fields.ADDRESSEES, ad);
                    if (ipAd == null) {
                        Log.d(TAG, "ERREUR : l'id " + ad + " n'existe pas");
                    } else if (!connexion2Client(newRequest, ipAd)) {
                        updateUserOnMap(request.getField(JSONMessage.Fields.ADDRESSEES));
                        if (!ipAd.equals(SingletonRegisterIDIP.getInstance().getIp(ad))) {
                            // mettre dans la message queue pour réeesayer
                            this.send.add(newRequest);
                        } else {
                            // mettre dans la waiting queue pour attendre avant de réessayer de le renvoyer
                            this.waitingQueue.add(newRequest);
                        }
                    }
                }
            }
        }

    }

    private void updateMap() {
        if (isNetworkAvailable()) {
            Log.d(TAG, "update MAP");
            ServerGetAllUserRequest serverGetRequest = new ServerGetAllUserRequest();
            serverGetRequest.execute(serverName + "?all_user=1");
            try {
                JSONArray jsonArray = serverGetRequest.get();
                Log.d(TAG, jsonArray.toString());
                SingletonRegisterIDIP.getInstance().updateAll(jsonArray);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUserOnMap(String username) {
        if (isNetworkAvailable()) {
            Log.d(TAG, "update user: " + username);
            ServerGetRequest serverGetRequest = new ServerGetRequest();
            serverGetRequest.execute(serverName + "?username=" + username);
            try {
                JSONObject jsonObject = serverGetRequest.get();
                Log.d(TAG, jsonObject.toString());
                SingletonRegisterIDIP.getInstance().updateUser(jsonObject.getString("username"), jsonObject.getString("ip"));
            } catch (InterruptedException | JSONException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void postIP2Server(JSONMessage message) {
        if (isNetworkAvailable()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", message.getField(JSONMessage.Fields.ID));
                jsonObject.put("password", message.getField(JSONMessage.Fields.PASSWORD));
                jsonObject.put("ip", Utils.getIPAddress(true));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "POST IP2Server request");
            ServerPostRequest serverPostRequest = new ServerPostRequest();
            try {
                serverPostRequest.execute(serverName + "?all_user=1", Utils.createQueryStringForParameters(jsonObject));
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean connexion2Client(JSONMessage jsmessage, String ipAd) {
        boolean success = true;

        final Handler handler = new Handler();
        return success;
    }
    
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void notifLucas() {

    }

    private void notifNico() {

    }
}
