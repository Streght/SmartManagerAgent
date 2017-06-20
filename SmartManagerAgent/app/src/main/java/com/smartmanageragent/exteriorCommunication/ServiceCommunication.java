package com.smartmanageragent.exteriorCommunication;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.smartmanageragent.smartagent.message.JSONMessage;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;

/**
 * Created by Maxime on 19/06/2017.
 */

public class ServiceCommunication extends IntentService {

    private String TAG = "Service communication: ";

    public String postIp = "POSTIP";
    public String getTable = "UPDATETABLE";
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public ServiceCommunication() {
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
                String sender = request.getField(JSONMessage.Fields.SENDER);
                if (sender != null) {
                    String ipSender = SingletonRegisterIDIP.getInstance().getIp(sender);
                    if (ipSender == null) {
                        updateMap();
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

    private void postIP2Server (JSONMessage message) {
 /*       if (isNetworkAvailable()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", "banane");
                jsonObject.put("password", "mdp");
                jsonObject.put("ip", "12.12.12.12");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("TestComActivity", "POST request");
            ServerPostRequest serverPostRequest = new ServerPostRequest();
            try {
                serverPostRequest.execute("http://calendar-matcher.spieldy.com/index.php?all_user=1", Utils.createQueryStringForParameters(jsonObject));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }*/
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
