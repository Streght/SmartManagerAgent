package com.smartmanageragent.exteriorcomm;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.agent.AgentImpl;
import com.smartmanageragent.smartagent.message.JSONMessage;
import com.smartmanageragent.smartagent.message.MessageQueue;
import com.smartmanageragent.smartagent.timeTable.Activity;
import com.smartmanageragent.smartagent.timeTable.TimeTable;
import com.smartmanageragent.smartagent.timeTable.TimeTableImpl;

import java.util.Date;
import java.util.Locale;

public class MyService extends Service {

    private final IBinder myBinder = new MyLocalBinder();



    private static MessageQueue<JSONMessage> receive;
    private static MessageQueue<JSONMessage> send;
    private static Agent<Date, Float, JSONMessage> agent;

    public static MessageQueue<JSONMessage> getReceive() {
        return receive;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO create and start agent.
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return myBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // TODO stop agent if possible.
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {

        String name = intent.getExtras().getString("username");
        receive = new MessageQueue<>();
        send = new MessageQueue<>();
        // Agents
        agent = new AgentImpl<>(name, receive, send);

        Activity<Float> empty = new Activity<>((float) 1, TimeTableImpl.unPriority, TimeTableImpl.unName);
        Date beg = new Date(0);
        Date end = new Date(10000);
        TimeTable<Date, Float> ttA1 = agent.getTimeTable();
        ttA1.addActivity(beg, empty);
        ttA1.addActivity(end, empty);

        new Thread(agent).start();

        return Service.START_REDELIVER_INTENT;
    }

    private void stopService() {

        // TODO
    }

    @TargetApi(Build.VERSION_CODES.N)
    public String getCurrentTime() {
        SimpleDateFormat dateformat =
                new SimpleDateFormat("HH:mm:ss MM/dd/yyyy", Locale.getDefault());
        return (dateformat.format(new Date()));
    }

    public class MyLocalBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }
}
