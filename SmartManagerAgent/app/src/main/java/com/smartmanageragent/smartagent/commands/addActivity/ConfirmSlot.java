package com.smartmanageragent.smartagent.commands.addActivity;


import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.agent.State;
import com.smartmanageragent.smartagent.commands.Command;
import com.smartmanageragent.smartagent.message.Message;
import com.smartmanageragent.smartagent.message.JSONMessage.Fields;
import com.smartmanageragent.smartagent.message.Serializer;
import com.smartmanageragent.smartagent.timeTable.Activity;
import com.smartmanageragent.smartagent.timeTable.slot.Slot;

import org.json.JSONObject;

import java.util.List;

@SuppressWarnings("unchecked")
public class ConfirmSlot<K, T> extends Command<K, T, String> {

	private Activity<T> activity;
	private Slot<T> slot;
	
	public ConfirmSlot(Message<String> message, Agent<K, T, String> agent) {
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
		State<K, T> state = this.agent.getState();
		List<Activity<T>> confList = (List<Activity<T>>) state.getAttribute(AcceptSlot.confList);
		// Agent must be waiting for a confirmation to add the activity in its time table
		if (confList!=null && confList.contains(this.activity)) {
			this.agent.insert((K) this.slot.getRef(), this.activity);
			confList.remove(this.activity);
			return true;
		}
		return false;
	}

}
