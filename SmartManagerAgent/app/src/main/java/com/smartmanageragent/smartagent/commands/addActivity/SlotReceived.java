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

public class SlotReceived<K, T> extends Command<K, T, String> {
	
	private Activity<T> activity;
	private Slot<T> slot;
	private String sender;
	
	@SuppressWarnings("unchecked")
	public SlotReceived(Message<String> message, Agent<K, T, String> agent) {
		super(message, agent);
		try {
			String jsonString = (String) message.getContent();
			JSONObject jsonObj = new JSONObject(jsonString);
			String strAct = jsonObj.getString(Fields.ACTIVITY.toString());
			this.activity = (Activity<T>) Serializer.deserialize(strAct);
			String strSlot = jsonObj.getString(Fields.SLOT.toString());
			this.slot = (Slot<T>) Serializer.deserialize(strSlot);
			this.sender = jsonObj.getString(Fields.SENDER.toString());
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
			// Sender : the sender of the slot, not this agent, message being sent to local already implies sender is
			// local agent
			jsonMessage.setField(Fields.SENDER, this.sender);
			// Command Type
			jsonMessage.setField(Fields.COMMAND, AskValidation.class.getName());
			// Addressee
			jsonMessage.setField(Fields.ADDRESSEES, JSONMessage.localAddressee);
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
