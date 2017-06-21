package com.smartmanageragent.smartagent.commands.list;

import java.io.NotSerializableException;
import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.commands.Command;
import com.smartmanageragent.smartagent.message.JSONMessage;
import com.smartmanageragent.smartagent.message.Message;
import com.smartmanageragent.smartagent.message.Serializer;
import com.smartmanageragent.smartagent.message.JSONMessage.Fields;
import com.smartmanageragent.smartagent.timeTable.Activity;

public class TTRequest<K, T> extends Command<K, T, String> {

    private String addressee;
    private Activity<T> activity;

    @SuppressWarnings("unchecked")
    public TTRequest(Message<String> message, Agent<K, T, String> agent) {
        super(message, agent);
        String jsonString = (String) message.getContent();
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(jsonString);
            this.addressee = jsonObj.getString(Fields.SENDER.toString());
            String strAct = jsonObj.getString(Fields.ACTIVITY.toString());
            this.activity = (Activity<T>) Serializer.deserialize(strAct);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean execute() {
        try {
            JSONMessage jsonMessage = new JSONMessage();
            // Sender
            jsonMessage.setField(Fields.SENDER, this.agent.getName());
            // Command Type
            jsonMessage.setField(Fields.COMMAND, TTAnswer.class.getName());
            // Addressee
            jsonMessage.setField(Fields.ADDRESSEES, this.addressee);
            // Activity
            jsonMessage.setField(Fields.ACTIVITY, Serializer.serialize((Serializable) this.activity));
            // Time table
            jsonMessage.setField(Fields.TIMETABLE, Serializer.serialize((Serializable) this.agent.getTimeTable()));
            // Sends message to the application
            this.getAgent().send(jsonMessage);
        } catch (NotSerializableException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
