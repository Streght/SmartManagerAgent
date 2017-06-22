package com.smartmanageragent.exteriorcomm;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;

import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.agent.AgentImpl;
import com.smartmanageragent.smartagent.message.JSONMessage;
import com.smartmanageragent.smartagent.message.MessageQueue;
import com.smartmanageragent.smartagent.message.Serializer;
import com.smartmanageragent.smartagent.timeTable.Activity;
import com.smartmanageragent.smartagent.timeTable.TimeTable;
import com.smartmanageragent.smartagent.timeTable.TimeTableImpl;

import java.io.NotSerializableException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {

    private final IBinder myBinder = new MyLocalBinder();
    private static MessageQueue<JSONMessage> receive;
    private static MessageQueue<JSONMessage> send;
    private static Agent<Date, Float, JSONMessage> agent;
    private SharedPreferences sharedPreferences = null;

    public static MessageQueue<JSONMessage> getReceive() {
        return receive;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences("com.smartmanageragent.exteriorcomm", MODE_PRIVATE);

        String receiveSaved = sharedPreferences.getString("receive", "");
        String sendSaved = sharedPreferences.getString("send", "");

        if (!(receiveSaved.equals(""))) {
            receive = (MessageQueue<JSONMessage>) Serializer.deserialize(receiveSaved);
        } else {
            receive = new MessageQueue<>();
        }
        if (!(sendSaved.equals(""))) {
            send = (MessageQueue<JSONMessage>) Serializer.deserialize(sendSaved);
        } else {
            send = new MessageQueue<>();
        }

        Timer t = new Timer();
        t.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        try {
                            editor.putString("timeTable", Serializer.serialize(agent.getTimeTable()));
                            editor.putString("receive", Serializer.serialize(receive));
                            editor.putString("send", Serializer.serialize(send));
                            editor.apply();
                        } catch (NotSerializableException e) {
                            e.printStackTrace();
                        }
                    }
                },
                10000,
                30000);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {

        String name = intent.getExtras().getString("username");
        agent = new AgentImpl<>(name, receive, send);

        String timeTableSaved = sharedPreferences.getString("timeTable", "");

        if (!(timeTableSaved.equals(""))) {

            TimeTable<Date, Float> timeTable = (TimeTable<Date, Float>) Serializer.deserialize(timeTableSaved);
            // TODO uncomment when available
            //agent.setTimeTable(timeTable);
        } else {
            Activity<Float> empty = new Activity<>((float) 1, TimeTableImpl.unPriority, TimeTableImpl.unName);
            Date beg = new Date(0);
            Date end = new Date(10000);

            TimeTable<Date, Float> timeTable = agent.getTimeTable();
            timeTable.addActivity(beg, empty);
            timeTable.addActivity(end, empty);
        }

        new Thread(agent).start();

        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent arg0) {
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
    }

    public TimeTable<Date, Float> getAgentTimeTable() {
        return agent.getTimeTable();
    }

    public class MyLocalBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }
}
