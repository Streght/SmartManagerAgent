package com.smartmanageragent.smartagent.commands.list;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.agent.Results;
import com.smartmanageragent.smartagent.agent.State;
import com.smartmanageragent.smartagent.commands.Command;
import com.smartmanageragent.smartagent.message.JSONMessage;
import com.smartmanageragent.smartagent.message.Message;
import com.smartmanageragent.smartagent.message.Serializer;
import com.smartmanageragent.smartagent.message.JSONMessage.Fields;
import com.smartmanageragent.smartagent.timeTable.Activity;
import com.smartmanageragent.smartagent.timeTable.TimeTable;

@SuppressWarnings("unchecked")
public class TTAnswer<K, T> extends Command<K, T, String> {

    public static final String results = "results";

    private String sender;
    private Activity<T> activity;
    private TimeTable<K, T> timeTable;

    public TTAnswer(Message<String> message, Agent<K, T, String> agent) {
        super(message, agent);
        String jsonString = (String) message.getContent();
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(jsonString);
            this.sender = jsonObj.getString(Fields.SENDER.toString());
            String strAct = jsonObj.getString(Fields.ACTIVITY.toString());
            this.activity = (Activity<T>) Serializer.deserialize(strAct);
            String strTT = jsonObj.getString(Fields.TIMETABLE.toString());
            this.timeTable = (TimeTable<K, T>) Serializer.deserialize(strTT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean execute() {
        State<K, T> state = this.agent.getState();
        HashMap<Activity<T>, List<String>> waitingMap =
                (HashMap<Activity<T>, List<String>>) state.getAttribute(AddActivity.waitingMap);
        HashMap<String, TimeTable<K, T>> otherTT =
                (HashMap<String, TimeTable<K, T>>) state.getAttribute(AddActivity.timeTables);
        if (waitingMap != null && otherTT != null && waitingMap.get(this.activity) != null) {
            // Removes the sender from the list of waited time tables
            List<String> nameList = waitingMap.get(this.activity);
            // Removes the sender from the list
            nameList.remove(this.sender);
            // Keeps the time table in mind
            otherTT.put(this.sender, this.timeTable);
            // If all the time tables for the given activity has been received
            if (nameList.size() <= 0) {
                // Agent is no longer waiting for time tables for the concerned activity
                waitingMap.remove(this.activity);
                return sendSlot(state);
            }
        }
        return false;
    }

    /**
     * Triggered when all time tables have been received
     *
     * @param state
     * @return
     */
    private boolean sendSlot(State<K, T> state) {
        try {
            JSONMessage jsonMessage = new JSONMessage();
            // Sender
            jsonMessage.setField(Fields.SENDER, this.agent.getName());
            // Command Type
            jsonMessage.setField(Fields.COMMAND, SlotReceived.class.getName());
            // Addressee
            jsonMessage.setField(Fields.ADDRESSEES, this.activity.getAttendees().toString());
            // Activity
            jsonMessage.setField(Fields.ACTIVITY, Serializer.serialize((Serializable) this.activity));
            // Gets the attendees time tables
            HashMap<String, TimeTable<K, T>> otherTT =
                    (HashMap<String, TimeTable<K, T>>) state.getAttribute(AddActivity.timeTables);
            List<String> attendees = this.activity.getAttendees();
            List<TimeTable<K, T>> ttList = new ArrayList<TimeTable<K, T>>();
            for (int i = 0; i < attendees.size(); i++) {
                ttList.add(otherTT.get(attendees.get(i)));
            }
            // Looks for the best slot
            Object slot = this.agent.findSlot((List<TimeTable<K, T>>) ttList, this.activity);
            // Slot found
            jsonMessage.setField(Fields.SLOT, Serializer.serialize((Serializable) slot));
            HashMap<Activity<T>, Results> results =
                    (HashMap<Activity<T>, Results>) state.getAttribute(TTAnswer.results);
            // Creates a map associating the activities, to the number of acceptances/refusals for the proposed slot
            if (results == null) {
                results = new HashMap<Activity<T>, Results>();
                state.addAttribute(TTAnswer.results, results);
            }
            results.put(this.activity, new Results(this.activity.getAttendees().size()));
            // Removes attendees time tables from intern state
            attendees = this.activity.getAttendees();
            for (int i = 0; i < attendees.size(); i++) {
                otherTT.remove(attendees.get(i));
            }
            // Sends message to the application
            this.getAgent().send(jsonMessage);
            return true;
        } catch (NotSerializableException e) {
            e.printStackTrace();
            return false;
        }
    }

}
