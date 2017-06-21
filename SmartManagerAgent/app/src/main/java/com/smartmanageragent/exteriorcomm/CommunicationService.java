package com.smartmanageragent.exteriorcomm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.smartmanageragent.smartagent.message.JSONMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;


public class CommunicationService extends IntentService {

    private String TAG = "Service communication: ";

    public String postIp = "POSTIP";
    public String getTable = "UPDATETABLE";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public CommunicationService() {
        super("CommunicationInter");
        SingletonRegisterIDIP.getInstance();
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getStringExtra(Intent.EXTRA_TEXT);
        Log.d(TAG, "Début service");
        // Do work here, based on the contents of dataString
        try {
            JSONMessage request = new JSONMessage(dataString);
            if (request.getField(JSONMessage.Fields.ACTIVITY).equals("LOCAL")) {
                // TODO : Envoyer à l'application
            } else if (request.getField(JSONMessage.Fields.ACTIVITY).equals(postIp)) {
                postIP2Server(request);
            } else if (request.getField(JSONMessage.Fields.ACTIVITY).equals(getTable)) {
                updateMap();
            }
            else {
                String addressee = request.getField(JSONMessage.Fields.ADDRESSEES);
                if (addressee != null) {
                    String ipSender = SingletonRegisterIDIP.getInstance().getIp(addressee);
                    /*if (ipSender == null) {
                        updateMap();
                        ipSender =  SingletonRegisterIDIP.getInstance().getIp(sender);
                    }*/
                    if (ipSender == null) {
                        Log.d(TAG, "ERREUR : l'id " + addressee+ " n'existe pas");
                    } else if (!connexion2Client(request)) {
                        updateUser(request.getField(JSONMessage.Fields.ADDRESSEES));
                        if (!ipSender.equals(SingletonRegisterIDIP.getInstance().getIp(addressee))) {
                            // TODO mettre dans la message queue pour réeesayer
                        } else {
                            // TODO mettre dans la waiting queue
                        }
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void updateMap () {
        if (isNetworkAvailable()) {
            Log.d(TAG, "update MAP");
            ServerGetAllUserRequest serverGetRequest = new ServerGetAllUserRequest();
            serverGetRequest.execute("http://calendar-matcher.spieldy.com/index.php?all_user=1");
            try {
                JSONArray jsonArray = serverGetRequest.get();
                Log.d(TAG, jsonArray.toString());
                SingletonRegisterIDIP.getInstance().updateAll(jsonArray);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateUser (String username) {
        if (isNetworkAvailable()) {
            Log.d(TAG, "update user: " + username);
            ServerGetRequest serverGetRequest = new ServerGetRequest();
            serverGetRequest.execute("http://calendar-matcher.spieldy.com/index.php?username="+username);
            try {
                JSONObject jsonObject = serverGetRequest.get();
                Log.d(TAG, jsonObject.toString());
                SingletonRegisterIDIP.getInstance().updateUser(jsonObject.getString("username"), jsonObject.getString("ip") );
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void postIP2Server (JSONMessage message) {
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
                serverPostRequest.execute("http://calendar-matcher.spieldy.com/index.php?all_user=1", Utils.createQueryStringForParameters(jsonObject));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean connexion2Client (JSONMessage jsmessage) {
        boolean success = true;

        return success;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
