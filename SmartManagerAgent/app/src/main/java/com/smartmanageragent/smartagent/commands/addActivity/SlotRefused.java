package com.smartmanageragent.smartagent.commands.addActivity;


import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.agent.Results;
import com.smartmanageragent.smartagent.agent.State;
import com.smartmanageragent.smartagent.commands.Command;
import com.smartmanageragent.smartagent.message.JSONMessage;
import com.smartmanageragent.smartagent.message.JSONMessage.Fields;
import com.smartmanageragent.smartagent.message.Message;
import com.smartmanageragent.smartagent.message.Serializer;
import com.smartmanageragent.smartagent.timeTable.Activity;
import com.smartmanageragent.smartagent.timeTable.slot.Slot;

import org.json.JSONObject;

import java.io.NotSerializableException;
import java.util.HashMap;

@SuppressWarnings("unchecked")
public class SlotRefused<K, T> extends Command<K, T, String> {
	
	private Activity<T> activity;
	private Slot<T> slot;
	
	public SlotRefused(Message<String> message, Agent<K, T, String> agent) {
		super(message, agent);
		try {
			String jsonString = (String) message.getContent();
			JSONObject jsonObj = new JSONObject(jsonString);
			String strAct = jsonObj.getString(Fields.ACTIVITY.toString());
			this.activity = (Activity<T>) Serializer.deserialize(strAct);
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
		State<K, T> state = this.getAgent().getState();
		HashMap<Activity<T>, Results> results = (HashMap<Activity<T>, Results>) state.getAttribute(TTAnswer.results);
		if (results != null) {
			Results res = results.get(this.activity);
			res.refusals++;
			// If the slot has been accepted
			if (res.finished()) {
				if (res.accepted()) {
					// Sends the attendees the confirmation the slot has been accepted
					this.sendConfirmation();
				}
				results.remove(this.activity);
				return true;
			}
		}
		return false;
	}

	/** Triggered when a slot has been accepted
	 * @return success
	 */
	private boolean sendConfirmation() {
		try {
			JSONMessage jsonMessage = new JSONMessage();
			// Sender
			jsonMessage.setField(Fields.SENDER, this.agent.getName());
			// Command Type
			jsonMessage.setField(Fields.COMMAND, ConfirmSlot.class.getName());
			// Addressee
			jsonMessage.setField(Fields.ADDRESSEES, this.activity.getAttendees().toString());
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
