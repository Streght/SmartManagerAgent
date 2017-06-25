package com.smartmanageragent.smartagent.commands.removeActivity;


import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.agent.Results;
import com.smartmanageragent.smartagent.agent.State;
import com.smartmanageragent.smartagent.commands.Command;
import com.smartmanageragent.smartagent.message.JSONMessage;
import com.smartmanageragent.smartagent.message.Message;
import com.smartmanageragent.smartagent.message.Serializer;
import com.smartmanageragent.smartagent.timeTable.Activity;
import com.smartmanageragent.smartagent.timeTable.slot.Slot;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unchecked")
public class RemoveActivity<K, T> extends Command<K, T, String> {
	
	public static final String removeResults = "removeResults";
	
	private Slot<T> slot;
	private Activity<T> activity;

	public RemoveActivity(Message<String> message, Agent<K, T, String> agent) {
		super(message, agent);
		try {
			String jsonString = message.getContent();
			JSONObject jsonObj = new JSONObject(jsonString);
			String strAct = jsonObj.getString(JSONMessage.Fields.ACTIVITY.toString());
			// Activity, created by the user
			this.activity = (Activity<T>) Serializer.deserialize(strAct);
			String strSlot = jsonObj.getString(JSONMessage.Fields.SLOT.toString());
			// Slot of the activity to delete
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
			jsonMessage.setField(JSONMessage.Fields.SENDER, this.agent.getName());
			// Command Type
			jsonMessage.setField(JSONMessage.Fields.COMMAND, RemoveRequest.class.getName());
			// Addressees
			List<String> otherAttendees = this.agent.otherAttendees(this.activity);
			jsonMessage.setField(JSONMessage.Fields.ADDRESSEES, otherAttendees.toString());
			// Activity
			jsonMessage.setField(JSONMessage.Fields.ACTIVITY, Serializer.serialize(this.activity));
			State<K, T> state = this.agent.getState();
			HashMap<Slot<T>, Results> results = 
					(HashMap<Slot<T>, Results>) state.getAttribute(RemoveActivity.removeResults);
			// Counts how many users accepted/refused to remove a given slot
			if (results==null) {
				results = new HashMap<Slot<T>, Results>();
				state.addAttribute(RemoveActivity.removeResults, results);
			}
			results.put(this.slot, new Results(otherAttendees.size()));
			// Sends message to the application
			this.getAgent().send(jsonMessage);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
