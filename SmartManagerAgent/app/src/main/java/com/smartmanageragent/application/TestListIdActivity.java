package com.smartmanageragent.application;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.smartmanageragent.exteriorCommunication.ClientThread;
import com.smartmanageragent.exteriorCommunication.SingletonRegisterIDIP;
import com.smartmanageragent.exteriorCommunication.ServerGetAllUserRequest;

import org.json.JSONArray;

import java.util.concurrent.ExecutionException;

public class TestListIdActivity extends AppCompatActivity {

    ClientThread clientThread;
    TextView textView1;
    Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list_id);


        textView1 = (TextView) this.findViewById(R.id.textView1);
        button1 = (Button) this.findViewById(R.id.button1);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Server GET request
                if (isNetworkAvailable()) {
                    Log.d("TestComActivity", "GET request");
                    ServerGetAllUserRequest serverGetRequest = new ServerGetAllUserRequest();
                    serverGetRequest.execute("http://calendar-matcher.spieldy.com/index.php?all_user=1");
                    try {
                        JSONArray jsonArray = serverGetRequest.get();
                        Log.d("TestComActivity", jsonArray.toString());
                        SingletonRegisterIDIP.getInstance().updateAll(jsonArray);
                        textView1.setText("List Users" + SingletonRegisterIDIP.getInstance().getListId().toString());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }




    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    private void clientEnd(){
        clientThread = null;
        //textViewState.setText("clientEnd()");

    }

}
