package com.smartmanageragent.exteriorCommunication;

import android.os.Message;
import android.util.Log;

import com.smartmanageragent.application.testCommunicationActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Nicolas on 17/11/2016.
 */



public class ClientThread extends Thread{

    String dstAddress;
    int dstPort;
    private boolean running;
    testCommunicationActivity.ClientHandler handler;

    Socket socket;
    PrintWriter printWriter;
    BufferedReader bufferedReader;

    public ClientThread(String addr, int port, testCommunicationActivity.ClientHandler handler) {
        super();
        Log.d("testCommunicationActivity","new client");
        dstAddress = addr;
        dstPort = port;
        this.handler = handler;
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    private void sendState(String state){
        handler.sendMessage(
                Message.obtain(handler,
                        testCommunicationActivity.ClientHandler.UPDATE_STATE, state));
    }

    public void txMsg(String msgToSend){
        if(printWriter != null){
            printWriter.println(msgToSend);
        }
    }

    public void sendJsonMsg(JSONObject jsonObject) {
        txMsg(jsonObject.toString());
    }

    @Override
    public void run() {
        Log.d("testCommunicationActivity","connecting...");
        sendState("connecting...");

        running = true;

        try {
            socket = new Socket(dstAddress, dstPort);
            sendState("connected");
            Log.d("testCommunicationActivity","client connected to " + dstAddress +": " + dstPort);

            OutputStream outputStream = socket.getOutputStream();
            printWriter = new PrintWriter(outputStream, true);

            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader =
                    new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            /*while(running){

                //bufferedReader block the code
                String line = bufferedReader.readLine();
                if(line != null){
                    handler.sendMessage(
                            Message.obtain(handler,
                                    testCommunicationActivity.ClientHandler.UPDATE_MSG, line));
                }

            }*/
            txMsg("hello from client");

            String line;

            Log.d("testCommunicationActivity","Client try to read msg from server");

            while (running && (line = bufferedReader.readLine()) != null) {

                Log.d("testCommunicationActivity","Server msg: " + line);
                //printWriter.println(line); // echo back to sender

                //handler.sendMessage(
                //        Message.obtain(handler,
                //                testCommunicationActivity.ClientHandler.UPDATE_MSG, line));
            }

        } catch (IOException e) {
            e.printStackTrace();
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

        handler.sendEmptyMessage(testCommunicationActivity.ClientHandler.UPDATE_END);
    }
}
