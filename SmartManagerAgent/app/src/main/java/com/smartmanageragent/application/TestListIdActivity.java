package com.smartmanageragent.application;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.smartmanageragent.exteriorcomm.ClientThreadTest;
import com.smartmanageragent.exteriorcomm.CommunicationService;
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
        final Intent mServiceIntent = new Intent(this, CommunicationService.class);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Server GET request
                JSONMessage jsMessage = new JSONMessage();
                jsMessage.setField(JSONMessage.Fields.COMMAND, CommunicationService.updateMap);

                textView1.setText("List Users" + SingletonRegisterIDIP.getInstance().getListId().toString());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, CommunicationService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
    }

    private CommunicationService communicationService;
    private boolean isBound = false;
    private ServiceConnection myConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            CommunicationService.MyLocalBinder binder = (CommunicationService.MyLocalBinder) service;
            communicationService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (isBound) {
            unbindService(myConnection);
            isBound = false;
        }
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
