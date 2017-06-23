package com.smartmanageragent.exteriorcomm;

import android.os.Message;
import android.util.Log;

import com.smartmanageragent.application.TestComActivity;
import com.smartmanageragent.smartagent.message.MessageQueue;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;


public class ServerThreadTest extends Thread {
    private Socket socket = null;
    private TestComActivity.ClientHandler handler;
    private PrintWriter printWriter;
    private MessageQueue<String> agentMQ;
    // private final GpioPinDigitalOutput[] pins;

    public ServerThreadTest(Socket socket, TestComActivity.ClientHandler handler, MessageQueue<String> mq/* , qGpioPinDigitalOutput[] pins */) {
        super("ServerThreadTest");
        this.socket = socket;
        this.handler = handler;
        this.agentMQ = mq;
        // this.pins = pins;
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

        Log.d("TestComActivity","New client on server");

        try {
           /* (PrintWriter
        } printWriter = new PrintWriter(this.socket.getOutputStream(), true);

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(this.socket.getInputStream()));) {*/
            Log.d("TestComActivity","Server try to read client msg");
            String inputLine;

            OutputStream outputStream = socket.getOutputStream();
            printWriter = new PrintWriter(outputStream, true);

            InputStream inputStream = socket.getInputStream();
            InputStreamReader inputStreamReader =
                    new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            final String outputLine;
            printWriter.println("Hello from server");
            while ((inputLine = bufferedReader.readLine()) != null) {
                Log.d("TestComActivity","Client msg: " + inputLine);
                handler.sendMessage(
                        Message.obtain(handler,
                                TestComActivity.ClientHandler.UPDATE_MSG, inputLine));
                printWriter.println(inputLine); // echo back to sender
            }
            Log.d("TestComActivity","Close server socket");
            this.socket.close();
        } catch (final IOException e) {
            Log.d("TestComActivity","Error in server: " + e.toString());
            e.printStackTrace();
        }
    }
}
