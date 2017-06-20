package com.smartmanageragent.application;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.smartmanageragent.exteriorcomm.ClientThread;
import com.smartmanageragent.exteriorcomm.ServerGetRequest;
import com.smartmanageragent.exteriorcomm.ServerThread;
import com.smartmanageragent.exteriorcomm.Utils;

import org.json.JSONObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutionException;

public class TestComActivity extends AppCompatActivity {

    ClientHandler clientHandler;
    ClientThread clientThread;
    TextView textView1;
    TextView textView2;
    TextView textView3;
    EditText editText1;
    EditText editText2;
    Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textView1 = (TextView) this.findViewById(R.id.textView1);
        textView2 = (TextView) this.findViewById(R.id.textView2);
        textView3 = (TextView) this.findViewById(R.id.textView3);
        editText1 = (EditText) this.findViewById(R.id.editText1);
        editText2 = (EditText) this.findViewById(R.id.editText2);
        button1 = (Button) this.findViewById(R.id.button1);
        //ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        //pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));



        // Server POST request
/*        if (isNetworkAvailable()) {
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
            }
        }*/

        // Server GET request
        if (isNetworkAvailable()) {
            Log.d("TestComActivity", "GET request");
            ServerGetRequest serverGetRequest = new ServerGetRequest();
            serverGetRequest.execute("http://calendar-matcher.spieldy.com/index.php?username=spieldy");
            try {
                JSONObject jsonObject = serverGetRequest.get();
                Log.d("TestComActivity", jsonObject.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }


        Log.d("TestComActivity","Helloo, create");

        // Creation of the server thread
        Thread t = new Thread(new Runnable() {
            public void run()
            {
                Log.d("TestComActivity","Create server thread");
                createServer();
                // Insert some method call here.
            }
        });
        t.start();

        //createServer();

        Log.d("TestComActivity","Client handler");

        // Creation of the client thread
        clientHandler = new TestComActivity.ClientHandler(this);
        //------Try to connect----
        final Handler handler = new Handler();
        final int delay = 10000; // Try to connect each 10 seconds
        handler.postDelayed(new Runnable(){
            public void run(){

                if(clientThread == null) {
                    //Connection on port 8000 and address 192.168.139.1
                    clientThread = new ClientThread(
                            /*"192.168.137.1"*/ editText1.getText().toString(),
                            8000,
                            clientHandler);
                    clientThread.start();
                }

                handler.postDelayed(this, delay);
            }
        }, delay);


        //Utils.getMACAddress("wlan0");
        //Utils.getMACAddress("eth0");
        String ipv4 = Utils.getIPAddress(true); // IPv4
        //Utils.getIPAddress(false);

        textView1.setText("Current IP: " + ipv4);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                class Sess extends AsyncTask<String, Integer, Void> {
                    protected Void doInBackground(String... lines) {
                        if(clientThread != null)
                            clientThread.txMsg(lines[0]);
                        return null;
                    }
                }
                new Sess().execute(editText2.getText().toString());

                //Test JSON object
                /*JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("username", "test");
                    jsonObject.put("password", "test");
                    jsonObject.put("ip", "test");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new Sess().execute(jsonObject.toString());*/
            }
        });

    }

    protected void createServer() {

        final int portNumber = 8000;
        final boolean listening = true;

        Log.d("TestComActivity","Port: " + portNumber);

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {

            while (listening) {
                new ServerThread(serverSocket.accept(), clientHandler).start();
            }
        } catch (final IOException e) {
            Log.d("TestComActivity","Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }

    public static class ClientHandler extends Handler {
        public static final int UPDATE_STATE = 0;
        public static final int UPDATE_MSG = 1;
        public static final int UPDATE_END = 2;
        private TestComActivity parent;

        public ClientHandler(TestComActivity parent) {
            super();
            this.parent = parent;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case UPDATE_STATE:
                    parent.updateState((String)msg.obj);
                    break;
                case UPDATE_MSG:
                    parent.updateRxMsg((String)msg.obj);
                    break;
                case UPDATE_END:
                    parent.clientEnd();
                    break;
                default:
                    super.handleMessage(msg);
            }

        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    View.OnClickListener buttonConnectOnClickListener =
            new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    //Connection on port 8000 and address 192.168.139.1
                    clientThread = new ClientThread(
                            "192.168.0.101",
                            8000,
                            clientHandler);
                    clientThread.start();
                }
            };

    private void updateState(String state){
        textView2.setText(state);
    }

    // Traitement du message recu
    private void updateRxMsg(String rxmsg){

        textView3.setText(rxmsg);


    }

    private void clientEnd(){
        clientThread = null;
        //textViewState.setText("clientEnd()");

    }

}
