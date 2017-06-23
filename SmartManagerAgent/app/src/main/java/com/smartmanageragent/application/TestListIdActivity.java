package com.smartmanageragent.application;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.smartmanageragent.exteriorcomm.ClientThreadTest;
import com.smartmanageragent.exteriorcomm.SingletonRegisterIDIP;
import com.smartmanageragent.smartagent.message.JSONMessage;

public class TestListIdActivity extends AppCompatActivity {

    ClientThreadTest clientThreadTest;
    TextView textView1;
    Button button1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_list_id);

        textView1 = (TextView) this.findViewById(R.id.textView1);
        button1 = (Button) this.findViewById(R.id.button1);
        /*final Intent mServiceIntent = new Intent(this, CommunicationService.class);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Server GET request
                /*if (isNetworkAvailable()) {
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

                JSONMessage jsMessage = new JSONMessage();
                jsMessage.setField(JSONMessage.Fields.SENDER, "theo");
                mServiceIntent.putExtra(Intent.EXTRA_TEXT, jsMessage.toString());
                startService(mServiceIntent);
                textView1.setText("List Users" + SingletonRegisterIDIP.getInstance().getListId().toString());
            }
        });*/
    }




    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    private void clientEnd(){
        clientThreadTest = null;
        //textViewState.setText("clientEnd()");

    }

}
