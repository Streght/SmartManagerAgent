package com.smartmanageragent.exteriorcomm;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ServerGetRequest extends AsyncTask<String, Void, JSONObject> {



    public  JSONObject doInBackground(String... urlString) {

        try {
            HttpURLConnection urlConnection = null;

            URL url = new URL(urlString[0]);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);

            urlConnection.setDoOutput(true);

            urlConnection.connect();

            BufferedReader br=new BufferedReader(new InputStreamReader(url.openStream()));

            char[] buffer = new char[1024];

            String jsonString;

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();

            jsonString = sb.toString();

            System.out.println("JSON: " + jsonString);
            Log.d("TestComActivity",jsonString);

            return new JSONObject(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
