package com.smartmanageragent.smartagent.commands.addActivity;


import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.commands.Command;
import com.smartmanageragent.smartagent.message.JSONMessage;
import com.smartmanageragent.smartagent.message.JSONMessage.Fields;
import com.smartmanageragent.smartagent.message.Message;
import com.smartmanageragent.smartagent.message.Serializer;
import com.smartmanageragent.smartagent.timeTable.Activity;
import com.smartmanageragent.smartagent.timeTable.slot.Slot;

import org.json.JSONObject;

import java.io.NotSerializableException;

public class RefuseSlot<K, T> extends Command<K, T, String> {
	
	private String addressee;
	private Activity<T> activity;
	private Slot<T> slot;
	
	@SuppressWarnings("unchecked")
	public RefuseSlot(Message<String> message, Agent<K, T, String> agent) {
		super(message, agent);
		try {
			String jsonString = (String) message.getContent();
			JSONObject jsonObj = new JSONObject(jsonString);
			// WARNING : message addressee must be the agent to send the result to, not this agent name !!
			this.addressee = jsonObj.getString(Fields.ADDRESSEES.toString());
			// WARNING : message sent by the application must contain the activity
			String strAct = jsonObj.getString(Fields.ACTIVITY.toString());
			this.activity = (Activity<T>) Serializer.deserialize(strAct);
			// WARNING : message sent by the application must contain the slot that has been refused !!
			String strSlot = jsonObj.getString(Fields.SLOT.toString());
			this.slot = (Slot<T>) Serializer.deserialize(strSlot);
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
			jsonMessage.setField(Fields.COMMAND, SlotRefused.class.getName());
			// Addressee
			jsonMessage.setField(Fields.ADDRESSEES, this.addressee);
			// Activity
			jsonMessage.setField(Fields.ACTIVITY, Serializer.serialize(this.activity));
			// Slot
			jsonMessage.setField(Fields.SLOT, Serializer.serialize(this.slot));
			// Sends message to the application, which will send the user a notification
			this.getAgent().send(jsonMessage);
		} catch (NotSerializableException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}