package com.smartmanageragent.smartagent.commands.addActivity;


import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.agent.State;
import com.smartmanageragent.smartagent.commands.Command;
import com.smartmanageragent.smartagent.message.JSONMessage;
import com.smartmanageragent.smartagent.message.JSONMessage.Fields;
import com.smartmanageragent.smartagent.message.Message;
import com.smartmanageragent.smartagent.message.Serializer;
import com.smartmanageragent.smartagent.timeTable.Activity;
import com.smartmanageragent.smartagent.timeTable.TimeTable;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class AddActivity<K, T> extends Command<K, T, String> {
	
	public static final String waitingMap = "waitingMap";
	public static final String timeTables = "timeTables";
	
	private Activity<T> activity;
	
	@SuppressWarnings("unchecked")
	public AddActivity(Message<String> message, Agent<K, T, String> agent) {
		super(message, agent);
		try {
			String jsonString = message.getContent();
			JSONObject jsonObj = new JSONObject(jsonString);
			String strAct = jsonObj.getString(Fields.ACTIVITY.toString());
			// Activity, created by the user
			this.activity = (Activity<T>) Serializer.deserialize(strAct);
		} catch (Exception e) {
			e.printStackTrace();
			this.buildError = true;
		}
	}

	@Override
	public boolean execute() {
		if (this.buildError)
			return false;
		try {
			JSONMessage jsonMessage = new JSONMessage();
			// Sender
			jsonMessage.setField(Fields.SENDER, this.agent.getName());
			// Command type
			jsonMessage.setField(Fields.COMMAND, TTRequest.class.getName());
			// Addressees
			jsonMessage.setField(Fields.ADDRESSEES, this.agent.otherAttendees(this.activity).toString());
			// Activity
			jsonMessage.setField(Fields.ACTIVITY, Serializer.serialize(this.activity));
			State<K, T> state = this.agent.getState();
			@SuppressWarnings("unchecked")
			HashMap<Activity<T>, List<String>> map =
				(HashMap<Activity<T>, List<String>>) state.getAttribute(AddActivity.waitingMap);
			// Creates a map associating the awaited activities, to the names of involved persons
			if (map==null) {
				map = new HashMap<Activity<T>, List<String>>();
				state.addAttribute(AddActivity.waitingMap, map);
			}
			map.put(this.activity, this.agent.otherAttendees(this.activity));
			// Creates a hash map to store other agents time tables
			if (state.getAttribute(AddActivity.timeTables)==null) {
				state.addAttribute(AddActivity.timeTables, new HashMap<String, TimeTable<K, T>>());
			}
			// Sends message to the application
			this.getAgent().send(jsonMessage);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
