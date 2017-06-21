package com.smartmanageragent.smartagent.commands.list;

import java.io.Serializable;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.agent.Results;
import com.smartmanageragent.smartagent.agent.State;
import com.smartmanageragent.smartagent.commands.Command;
import com.smartmanageragent.smartagent.message.Message;
import com.smartmanageragent.smartagent.message.Serializer;
import com.smartmanageragent.smartagent.message.JSONMessage.Fields;
import com.smartmanageragent.smartagent.timeTable.Activity;

@SuppressWarnings("unchecked")
public class SlotRefused<K, T> extends Command<K, T, String> {

    private Activity<T> activity;
    private Serializable slot;

    public SlotRefused(Message<String> message, Agent<K, T, String> agent) {
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
        State<K, T> state = this.getAgent().getState();
        HashMap<Activity<T>, Results> results = (HashMap<Activity<T>, Results>) state.getAttribute(TTAnswer.results);
        if (results != null) {
            Results res = results.get(this.activity);
            res.refusals++;
            // If the slot has been accepted
            if (res.finished()) {
                if (res.accepted()) {
                    // Adds the activity in the time table
                    this.agent.getTimeTable().addActivity((K) this.slot, this.activity);
                    // TODO : send confirmation : ConfirmSlot !!
                }
                results.remove(this.activity);
                return true;
            }
        }
        return false;
    }

}
