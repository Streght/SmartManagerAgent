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

public class SlotReceived<K, T> extends Command<K, T, String> {

    private Activity<T> activity;
    private Serializable slot;

    @SuppressWarnings("unchecked")
    public SlotReceived(Message<String> message, Agent<K, T, String> agent) {
        super(message, agent);
        String jsonString = (String) message.getContent();
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(jsonString);
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
        try {
            JSONMessage jsonMessage = new JSONMessage();
            // Sender
            jsonMessage.setField(Fields.SENDER, this.agent.getName());
            // Command Type
            jsonMessage.setField(Fields.COMMAND, AskValidation.class.getName());
            // Addressee
            jsonMessage.setField(Fields.ADDRESSEES, JSONMessage.localAddressee);
            // Activity
            jsonMessage.setField(Fields.ACTIVITY, Serializer.serialize((Serializable) this.activity));
            // Slot
            jsonMessage.setField(Fields.SLOT, Serializer.serialize((Serializable) this.slot));
            // Sends message to the application, which will send the user a notification
            this.getAgent().send(jsonMessage);
        } catch (NotSerializableException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
