package com.smartmanageragent.smartagent.commands.addActivity;

import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import agent.Agent;
import agent.State;
import agent.TimeOut;
import commands.Command;
import message.JSONMessage;
import message.Message;
import message.Serializer;
import message.JSONMessage.Fields;
import timeTable.Activity;
import timeTable.slot.Slot;

@SuppressWarnings("unchecked")
public class AcceptSlot<K, T> extends Command<K, T, String> {
	
	public static final String confList = "confList";
	
	private String addressee;
	private Activity<T> activity;
	private Slot<T> slot;
	
	public AcceptSlot(Message<String> message, Agent<K, T, String> agent) {
		super(message, agent);
		try {
			String jsonString = (String) message.getContent();
			JSONObject jsonObj = new JSONObject(jsonString);
			// WARNING : message addressee must be the agent to send the result to, not this agent name !!
			this.addressee = jsonObj.getString(Fields.ADDRESSEES.toString());
			// WARNING : message sent by the application must contain the activity
			String strAct = jsonObj.getString(Fields.ACTIVITY.toString());
			this.activity = (Activity<T>) Serializer.deserialize(strAct);
			// WARNING : message sent by the application must contain the slot that has been accepted !!
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
			State<K, T> state = this.agent.getState();
			// Activities for which the agent is waiting for the confirmation, ie activity has been accepted
			List<Activity<T>> confirmations = (List<Activity<T>>) state.getAttribute(AcceptSlot.confList);
			if (confirmations==null) {
				confirmations = new ArrayList<Activity<T>>();
				state.addAttribute(AcceptSlot.confList, confirmations);
			}
			confirmations.add(this.activity);
			JSONMessage jsonMessage = new JSONMessage();
			// Sender
			jsonMessage.setField(Fields.SENDER, this.agent.getName());
			// Command Type
			jsonMessage.setField(Fields.COMMAND, SlotAccepted.class.getName());
			// Addressee
			jsonMessage.setField(Fields.ADDRESSEES, this.addressee);
			// Activity
			jsonMessage.setField(Fields.ACTIVITY, Serializer.serialize(this.activity));
			// Slot
			jsonMessage.setField(Fields.SLOT, Serializer.serialize(this.slot));
			// Sends message to the application, which will send the user a notification
			this.getAgent().send(jsonMessage);
			// If no confirmation has been received after a while, the agent is no longer waiting
			this.setCancel();
		} catch (NotSerializableException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void setCancel() throws NotSerializableException {
		JSONMessage jsonMessage = new JSONMessage();
		// Sender
		jsonMessage.setField(Fields.SENDER, this.agent.getName());
		// Command Type
		jsonMessage.setField(Fields.COMMAND, ConfCancel.class.getName());
		// Addressee
		jsonMessage.setField(Fields.ADDRESSEES, this.agent.getName());
		// Activity
		jsonMessage.setField(Fields.ACTIVITY, Serializer.serialize(this.activity));
		// A message is launched after a while
		TimeOut<String> to = new TimeOut<String>(this.agent.getReceiving(), jsonMessage, ConfCancel.timeOut);
		to.runTimer();
	}
	
}
