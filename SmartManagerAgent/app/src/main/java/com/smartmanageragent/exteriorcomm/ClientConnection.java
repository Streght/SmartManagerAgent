package com.smartmanageragent.exteriorcomm;

import android.os.Handler;
import android.util.Log;

import com.smartmanageragent.smartagent.message.JSONMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientConnection {

    private String dstAddress;
    private int dstPort;
    private boolean running;
    private Handler handler;
    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private JSONMessage jsM2send;
    private String TAG = "Client2OtherPhone";

    public ClientConnection(String addr, int port, JSONMessage jsMessage) {
        super();
        Log.d(TAG,"new client");
        dstAddress = addr;
        dstPort = port;
        this.handler = new Handler();
        this.jsM2send = jsMessage;
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    public void txMsg(String msgToSend){
        if(printWriter != null){
            printWriter.println(msgToSend);
        }
    }

    public void sendJsonMsg(JSONMessage jsonMessage) {
        txMsg(jsonMessage.toString());
    }

    public boolean connection() {
        Log.d(TAG,"connecting...");
        boolean success = true;

        try {
            socket = new Socket(dstAddress, dstPort);
            Log.d(TAG,"client connected to " + dstAddress +": " + dstPort);

            OutputStream outputStream = socket.getOutputStream();
            printWriter = new PrintWriter(outputStream, true);

            sendJsonMsg(jsM2send);


            // TODO : recevoir accusé de réception envoyé par serveur
            // On s'en fiche
            /* InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader =
                    new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            Log.d(TAG,"Client try to read msg from server");
            while (running && (line = bufferedReader.readLine()) != null) {
                Log.d(TAG,"Server msg: " + line);
            }*/

        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        } finally {
            if(bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(printWriter != null){
                printWriter.close();
            }
            if(socket != null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }
}
