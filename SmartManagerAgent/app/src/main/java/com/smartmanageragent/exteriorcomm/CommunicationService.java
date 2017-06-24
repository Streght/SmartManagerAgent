package com.smartmanageragent.exteriorcomm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.smartmanageragent.application.CalendarActivity;
import com.smartmanageragent.application.R;
import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.agent.AgentImpl;
import com.smartmanageragent.smartagent.message.JSONMessage;
import com.smartmanageragent.smartagent.message.MessageQueue;
import com.smartmanageragent.smartagent.message.Serializer;
import com.smartmanageragent.smartagent.message.WaitingQueue;
import com.smartmanageragent.smartagent.timeTable.Activity;
import com.smartmanageragent.smartagent.timeTable.TimeTable;
import com.smartmanageragent.smartagent.timeTable.TimeTableImpl;
import com.smartmanageragent.smartagent.timeTable.slot.Slot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

public class CommunicationService extends Service {

    public static String postIp = "POSTIP";
    public static String updateMap = "UPDATEMAP";
    private static String serverName = "http://calendar-matcher.spieldy.com/index.php";
    private static int portNumber = 8000;
    private static MessageQueue<String> receive;
    private static MessageQueue<String> send;
    private static Agent<Date, Float, String> agent;
    private static WaitingQueue<String> waitingQueue;
    private final IBinder myBinder = new MyLocalBinder();
    private SharedPreferences sharedPreferences = null;
    private String name = "";

    public String getName() {
        return name;
    }

    public MessageQueue<String> getReceive() {
        return receive;
    }

    public MessageQueue<String> getSend() {
        return send;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences("com.smartmanageragent.exteriorcomm", MODE_PRIVATE);

        String agentname = sharedPreferences.getString("agentname", "");
        String receiveSaved = sharedPreferences.getString("receive", "");
        String sendSaved = sharedPreferences.getString("send", "");

        if (!(agentname.equals(""))) {
            name = agentname;
        } else {
            name = CalendarActivity.getUser();
        }
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
                            editor.putString("agentname", name);
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

        //String name = intent.getExtras().getString("username");

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

        // Création du serveur en local
        Thread threadServer = new Thread(new Runnable() {
            public void run() {
                Log.d(TAG, "Create local server thread");
                createServer();
            }
        });
        threadServer.start();

        // Thread chargé de vider la MessageQueue des messages que l'agent veut envoyer
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

        // On vide la waitingQueue dans la MessageQueue send toutes les 30 minutes
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                //TODO : Pour tester. A supprimer après
                Log.d(TAG, "Vidage WaitingQueue vers send: " + waitingQueue.toString());
                waitingQueue.emptyToMessageQueue();
                Log.d(TAG, "Fin vidage: waitingQueue =" + waitingQueue.toString() + " sending queue= " + send.toString());
                handler.postDelayed(this, waitingQueue.getWaitingTime());
            }
        }, waitingQueue.getWaitingTime());

        SingletonRegisterIDIP.getInstance(); // On initialise la map
    }

    @SuppressWarnings("unchecked")
    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent arg0) {

        JSONMessage registration = CommApp.postIp(agent.getName(), "toor");
        send.add(registration);

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

    // Créer le serveur
    protected void createServer() {
        Log.d(TAG, "Port: " + portNumber);
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (true) {
                new ServerThread(serverSocket.accept(), receive).start();
            }
        } catch (final IOException e) {
            Log.d(TAG, "Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }

    // Traite les requêtes envoyées par l'agent local
    private void fonctionMagique(JSONMessage request) {
        Log.d(TAG, "Début Envoi message");
        if (request.getField(JSONMessage.Fields.ADDRESSEES).equals("LOCAL")) {
            // TODO set conditions
            if (request.getField(JSONMessage.Fields.COMMAND).equals("")) {

                Activity<Float> activity = (Activity<Float>) Serializer.deserialize(request.getField(JSONMessage.Fields.ACTIVITY));

                NotificationCompat.Builder mBuilder =
                        (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.ic_meeting_accepted)
                                .setContentTitle(getResources().getString(R.string.accepted))
                                .setContentText(activity.getName());
                int monID = 1;
                NotificationManager monManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                monManager.notify(monID, mBuilder.build());

            } else if (request.getField(JSONMessage.Fields.COMMAND).equals("")) {

                Activity<Float> activity = (Activity<Float>) Serializer.deserialize(request.getField(JSONMessage.Fields.ACTIVITY));
                Slot<Float> slot = (Slot<Float>) Serializer.deserialize(request.getField(JSONMessage.Fields.SLOT));

                String name = activity.getName();

                Date beg = (Date) slot.getRef();
                Calendar beginning = Calendar.getInstance();
                beginning.setTime(beg);

                float duration = activity.getLength();
                int hourLength = (int) duration / 60;
                int minutesLength = (int) duration % 60;

                Calendar ending = Calendar.getInstance();
                ending.setTime(beg);
                ending.set(ending.get(Calendar.YEAR),
                        ending.get(Calendar.MONTH),
                        ending.get(Calendar.DATE),
                        ending.get(Calendar.HOUR_OF_DAY) + hourLength,
                        ending.get(Calendar.MINUTE) + minutesLength);

                String attendees = android.text.TextUtils.join(", ", activity.getAttendees());

                NotificationCompat.Builder mBuilder =
                        (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.ic_meeting_accepted)
                                .setContentTitle(getResources().getString(R.string.notif_txt))
                                .setContentText(name);

                Intent resultIntent = new Intent(this, CalendarActivity.class);
                resultIntent.putExtra("notification", true);
                resultIntent.putExtra("name", name);
                resultIntent.putExtra("attendees", attendees);
                resultIntent.putExtra("meetingBeginning", beginning.getTimeInMillis());
                resultIntent.putExtra("timeZoneBeginning", beginning.getTimeZone().getID());
                resultIntent.putExtra("meetingEnding", ending.getTimeInMillis());
                resultIntent.putExtra("timeZoneEnding", ending.getTimeZone().getID());

                PendingIntent resultPendingIntent =
                        PendingIntent.getActivity(
                                this,
                                0,
                                resultIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                mBuilder.setContentIntent(resultPendingIntent);

                int mNotificationId = 1;
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.notify(mNotificationId, mBuilder.build());


            }
        } else if (request.getField(JSONMessage.Fields.COMMAND).equals(postIp)) {
            postIP2Server(request);
        } else if (request.getField(JSONMessage.Fields.COMMAND).equals(updateMap)) { // Normalement plus utilisé.
            updateMap();
        } else {
            String addresseeListString = request.getField(JSONMessage.Fields.ADDRESSEES);
            List<String> addresseeList = new ArrayList<>(Arrays.asList(addresseeListString.split(",")));
            if (addresseeList != null) {
                for (String ad : addresseeList) {
                    boolean flagServerContacted = false;
                    String ipAd = SingletonRegisterIDIP.getInstance().getIp(ad); // On regarde si on a deja contacte notre petit(e) copain(e)
                    JSONMessage newRequest = request;
                    newRequest.setField(JSONMessage.Fields.ADDRESSEES, ad);
                    if (ipAd == null) { // Le user n'avait pas déjà été contacté et n'est donc pas enregistre dans la map
                        ipAd = getIpUser(ad); // On demande au serveur
                        flagServerContacted = true;
                        if (ipAd == null) {
                            Log.d(TAG, "ERREUR : l'id " + ad + " n'existe pas");
                        } else {
                            SingletonRegisterIDIP.getInstance().updateUser(ad, ipAd); // Ajout de notre nouveau copain(e) a la map
                        }
                    }
                    if (!connexion2Client(newRequest, ipAd)) { // Si la connexion entre clients a echoue
                        if (!flagServerContacted) { // Si l'ip n'a pas ete recuperee sur le serveur
                            updateUserOnMap(ad); // On update la map
                            if (!ipAd.equals(SingletonRegisterIDIP.getInstance().getIp(ad))) {
                                // mettre dans la message queue pour réeesayer
                                this.send.add(newRequest);
                            }
                        } else {
                            // mettre dans la waiting queue pour attendre avant de réessayer de le renvoyer
                            this.waitingQueue.add(newRequest);
                        }
                    }
                }
            }
        }

    }

    // Inutile
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

    private String getIpUser(String username) {
        String ip = null;
        if (isNetworkAvailable()) {
            Log.d(TAG, "update user: " + username);
            ServerGetRequest serverGetRequest = new ServerGetRequest();
            serverGetRequest.execute(serverName + "?username=" + username);
            try {
                JSONObject jsonObject = serverGetRequest.get();
                Log.d(TAG, jsonObject.toString());
                ip = jsonObject.getString("ip");
            } catch (InterruptedException | JSONException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return ip;
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
        Handler clientHandler = new Handler();
        ClientConnection clientConnection = new ClientConnection(ipAd, portNumber, clientHandler, jsmessage);
        boolean success = clientConnection.connection();
        return success;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public class MyLocalBinder extends Binder {
        public CommunicationService getService() {
            return CommunicationService.this;
        }
    }

}
