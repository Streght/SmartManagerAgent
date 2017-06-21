package com.smartmanageragent.smartagent;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.agent.AgentImpl;
import com.smartmanageragent.smartagent.message.MessageQueue;
import com.smartmanageragent.smartagent.timeTable.TimeTable;
import com.smartmanageragent.smartagent.timeTable.TimeTableImpl;

import java.util.Date;

public class AgentService extends IntentService {

    private static final String START_AGENT = "com.smartmanageragent.smartagent.agent.Agent.START";
    protected MessageQueue receiving = null;
    protected MessageQueue sending = null;
    public static Agent agentTest = null;

    public AgentService() {
        super("AgentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (START_AGENT.equals(action)) {
                handleStartAgent();
            }
        }
    }

    private void handleStartAgent() {
        agentTest = new AgentImpl(receiving,sending);
        agentTest.run();
    }

    public static TimeTable<Date,Float> getAgentTimeTable(){
        return agentTest.getTimeTable();
    }

}
