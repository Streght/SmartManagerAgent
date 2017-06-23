package com.smartmanageragent.exteriorcomm;

import android.util.Log;

import com.smartmanageragent.smartagent.message.JSONMessage;
import com.smartmanageragent.smartagent.message.MessageQueue;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ServerThread extends Thread {
    private Socket socket;
    private PrintWriter printWriter;
    private MessageQueue<String> agentMQ;
    private String TAG = "ServeurLocal";

    public ServerThread(Socket socket, MessageQueue<String> mq) {
        super("ServerLocal");
        this.socket = socket;
        this.agentMQ = mq;
    }


    private void txMsg(String msgToSend){
        if(printWriter != null){
            printWriter.println(msgToSend);
        }
    }

    public void sendJsonMsg(JSONObject jsonObject) {
        txMsg(jsonObject.toString());
    }

    @Override
    public void run() {

        Log.d(TAG,"New client on server");

        try {
            Log.d(TAG,"Server try to read client msg");

            String inputLine;
            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader =
                    new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String stringJsMessage = "";

            while ((inputLine = bufferedReader.readLine()) != null) {
                Log.d(TAG,"Client msg: " + inputLine);
                stringJsMessage += inputLine;
            }

            JSONMessage jsMessage= new JSONMessage(stringJsMessage);
            agentMQ.add(jsMessage);
            Log.d(TAG,"Close server socket");

            this.socket.close();
        } catch (final IOException e) {
            Log.d(TAG,"Error in server: " + e.toString());
            e.printStackTrace();
        }
    }
}