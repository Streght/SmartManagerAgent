package com.smartmanageragent.smartagent.commands.addActivity;


import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.commands.Command;
import com.smartmanageragent.smartagent.message.JSONMessage;
import com.smartmanageragent.smartagent.message.JSONMessage.Fields;
import com.smartmanageragent.smartagent.message.Message;
import com.smartmanageragent.smartagent.message.Serializer;
import com.smartmanageragent.smartagent.timeTable.Activity;

import org.json.JSONObject;

import java.io.NotSerializableException;

public class TTRequest<K, T> extends Command<K, T, String> {

	private String addressee;
	private Activity<T> activity;
	
	@SuppressWarnings("unchecked")
	public TTRequest(Message<String> message, Agent<K, T, String> agent) {
		super(message, agent);
		try {
			String jsonString = (String) message.getContent();
			JSONObject jsonObj = new JSONObject(jsonString);
			this.addressee = jsonObj.getString(Fields.SENDER.toString());
			String strAct = jsonObj.getString(Fields.ACTIVITY.toString());
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
			// Command Type
			jsonMessage.setField(Fields.COMMAND, TTAnswer.class.getName());
			// Addressee
			jsonMessage.setField(Fields.ADDRESSEES, this.addressee);
			// Activity
			jsonMessage.setField(Fields.ACTIVITY, Serializer.serialize(this.activity));
			// Time table
			jsonMessage.setField(Fields.TIMETABLE, Serializer.serialize(this.agent.getTimeTable()));
			// Sends message to the application
			this.getAgent().send(jsonMessage);
		} catch (NotSerializableException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
