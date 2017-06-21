package com.smartmanageragent.smartagent.commands.list;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.commands.Command;
import com.smartmanageragent.smartagent.message.Message;
import com.smartmanageragent.smartagent.message.Serializer;
import com.smartmanageragent.smartagent.message.JSONMessage.Fields;
import com.smartmanageragent.smartagent.timeTable.Activity;

public class ConfirmSlot<K, T> extends Command<K, T, String> {

    private Activity<T> activity;
    private Serializable slot;

    @SuppressWarnings("unchecked")
    public ConfirmSlot(Message<String> message, Agent<K, T, String> agent) {
        super(message, agent);
        String jsonString = (String) message.getContent();
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(jsonString);
            // TODO
            String strAct = jsonObj.getString(Fields.ACTIVITY.toString());
            this.activity = (Activity<T>) Serializer.deserialize(strAct);
            String strSlot = jsonObj.getString(Fields.SLOT.toString());
            this.slot = Serializer.deserialize(strSlot);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean execute() {
        // TODO : test an attribute waitConfirmation

        // TODO Auto-generated method stub
        return false;
    }

}
