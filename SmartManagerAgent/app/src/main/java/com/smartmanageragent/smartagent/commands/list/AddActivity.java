package com.smartmanageragent.smartagent.commands.list;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.commands.Command;
import com.smartmanageragent.smartagent.message.JSONMessage;
import com.smartmanageragent.smartagent.message.JSONMessage.Fields;
import com.smartmanageragent.smartagent.message.Message;
import com.smartmanageragent.smartagent.message.Serializer;
import com.smartmanageragent.smartagent.timeTable.Activity;

public class AddActivity<K, T> extends Command<K, T, String> {
	
	private Activity<T> activity;
	
	@SuppressWarnings("unchecked")
	public AddActivity(Message<String> message, Agent<K, T, String> agent) throws JSONException {
		super(message, agent);
		String jsonString = (String) message.getContent();
		JSONObject jsonObj = new JSONObject(jsonString);
		String strAct = jsonObj.getString("Activity");
		// Activity, created by the user
		this.activity = (Activity<T>) Serializer.deserialize(strAct);
	}

	@Override
	public boolean execute() {
		try {
			JSONMessage jsonMessage = new JSONMessage();
			jsonMessage.setField(Fields.SENDER, this.agent.getName());
			// Command type
			jsonMessage.setField(Fields.COMMAND, "TTRequest");
			// Addressees
			jsonMessage.setField(Fields.ADDRESSEES, this.activity.getAttendees().toString());
			// Activity
			jsonMessage.setField(Fields.ACTIVITY, Serializer.serialize((Serializable) this.activity));
			// Sends message to the application
			this.getAgent().send(jsonMessage);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
