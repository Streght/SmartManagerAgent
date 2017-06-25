package com.smartmanageragent.smartagent.commands.addActivity;


import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.agent.State;
import com.smartmanageragent.smartagent.commands.Command;
import com.smartmanageragent.smartagent.message.Message;
import com.smartmanageragent.smartagent.message.JSONMessage.Fields;
import com.smartmanageragent.smartagent.message.Serializer;
import com.smartmanageragent.smartagent.timeTable.Activity;

import org.json.JSONObject;

import java.util.List;

@SuppressWarnings("unchecked")
public class ConfCancel<K, T> extends Command<K, T, String> {
	
	private Activity<T> activity;
	public static final int timeOut = 10*60;
	
	public ConfCancel(Message<String> message, Agent<K, T, String> agent) {
		super(message, agent);
		try {
			String jsonString = (String) message.getContent();
			JSONObject jsonObj = new JSONObject(jsonString);
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
		State<K, T> state = this.getAgent().getState();
		List<Activity<T>> confirmations = (List<Activity<T>>) state.getAttribute(AcceptSlot.confList);
		if (confirmations != null) {
			// Removes the awaited confirmation
			return confirmations.remove(this.activity);
		}
		return false;
	}
	
}
