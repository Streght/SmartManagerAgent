package ca.uqac.socketsapp;

import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Nicolas on 06/06/2017.
 */


public class ServerThread extends Thread {
    private Socket socket = null;
    MainActivity.ClientHandler handler;
    private PrintWriter printWriter;
    // private final GpioPinDigitalOutput[] pins;

    public ServerThread(Socket socket, MainActivity.ClientHandler handler/* , GpioPinDigitalOutput[] pins */) {
        super("ServerThread");
        this.socket = socket;
        this.handler = handler;
        // this.pins = pins;
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

        Log.d("MainActivity","New client on server");

        try {
           /* (PrintWriter
        } printWriter = new PrintWriter(this.socket.getOutputStream(), true);

        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(this.socket.getInputStream()));) {*/
            Log.d("MainActivity","Server try to read client msg");
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
                Log.d("MainActivity","Client msg: " + inputLine);
                handler.sendMessage(
                        Message.obtain(handler,
                                MainActivity.ClientHandler.UPDATE_MSG, inputLine));
                printWriter.println(inputLine); // echo back to sender
            }
            Log.d("MainActivity","Close server socket");
            this.socket.close();
        } catch (final IOException e) {
            Log.d("MainActivity","Error in server: " + e.toString());
            e.printStackTrace();
        }
    }
}
