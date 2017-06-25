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
import com.smartmanageragent.smartagent.timeTable.TimeTable;
import com.smartmanageragent.smartagent.timeTable.slot.Slot;

import org.json.JSONObject;

import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unchecked")
public class TTAnswer<K, T> extends Command<K, T, String> {

	public static final String results = "results";

	private String sender;
	private Activity<T> activity;
	private TimeTable<K, T> timeTable;

	public TTAnswer(Message<String> message, Agent<K, T, String> agent) {
		super(message, agent);
		try {
			String jsonString = (String) message.getContent();
			JSONObject jsonObj = new JSONObject(jsonString);
			this.sender = jsonObj.getString(Fields.SENDER.toString());
			String strAct = jsonObj.getString(Fields.ACTIVITY.toString());
			this.activity = (Activity<T>) Serializer.deserialize(strAct);
			String strTT = jsonObj.getString(Fields.TIMETABLE.toString());
			this.timeTable = (TimeTable<K, T>) Serializer.deserialize(strTT);
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
		HashMap<Activity<T>, List<String>> waitingMap =
				(HashMap<Activity<T>, List<String>>) state.getAttribute(AddActivity.waitingMap);
		HashMap<String, TimeTable<K, T>> otherTT = 
				(HashMap<String, TimeTable<K, T>>) state.getAttribute(AddActivity.timeTables);
		if (waitingMap!=null && otherTT!=null && waitingMap.get(this.activity)!=null) {
			// Removes the sender from the list of waited time tables
			List<String> nameList = waitingMap.get(this.activity);
			// Removes the sender from the list
			nameList.remove(this.sender);
			// Keeps the time table in mind
			otherTT.put(this.sender, this.timeTable);
			// If all the time tables for the given activity has been received
			if (nameList.size()<=0) {
				// Agent is no longer waiting for time tables for the concerned activity
				waitingMap.remove(this.activity);
				// Computes the activity slot
				Slot<T> slot = this.computeSlot(state);
				// Sends the computed slot to all other agents
				boolean send = this.sendSlot(state, slot);
				// Asks directly this agent user its confirmation
				boolean conf = this.askConfirmation(slot);
				return send && conf;
			}
		}
		return false;
	}

	/** Computes the activity slot
	 * @param state
	 * @return slot
	 */
	private Slot<T> computeSlot(State<K, T> state) {
		List<String> otherAttendees = this.agent.otherAttendees(this.activity);
		// Gets the attendees time tables
		HashMap<String, TimeTable<K, T>> otherTT = 
				(HashMap<String, TimeTable<K, T>>) state.getAttribute(AddActivity.timeTables);
		List<TimeTable<K, T>> ttList = new ArrayList<TimeTable<K, T>>();
		for (int i=0; i<otherAttendees.size(); i++) {
			ttList.add(otherTT.get(otherAttendees.get(i)));
		}
		// Looks for the best slot
		Slot<T> slot = this.agent.findSlot((List<TimeTable<K, T>>) ttList, this.activity);
		// Removes other attendees time tables from intern state
		for(int i=0; i<otherAttendees.size(); i++) {
			otherTT.remove(otherAttendees.get(i));
		}
		return slot;
	}

	/** Sends the computed slot to all other agents
	 * @param state
	 * @param slot
	 * @return success
	 */
	private boolean sendSlot(State<K, T> state, Slot<T> slot) {
		try {
			JSONMessage jsonMessage = new JSONMessage();
			// Sender
			jsonMessage.setField(Fields.SENDER, this.agent.getName());
			// Command Type
			jsonMessage.setField(Fields.COMMAND, SlotReceived.class.getName());
			// Addressees
			List<String> otherAttendees = this.agent.otherAttendees(this.activity);
			jsonMessage.setField(Fields.ADDRESSEES, otherAttendees.toString());
			// Activity
			jsonMessage.setField(Fields.ACTIVITY, Serializer.serialize(this.activity));
			// Slot found 
			jsonMessage.setField(Fields.SLOT, Serializer.serialize(slot));
			HashMap<Activity<T>, Results> results =
					(HashMap<Activity<T>, Results>) state.getAttribute(TTAnswer.results);
			// Creates a map associating the activities, to the number of acceptances/refusals for the proposed slot
			if (results==null) {
				results = new HashMap<Activity<T>, Results>();
				state.addAttribute(TTAnswer.results, results);
			}
			results.put(this.activity, new Results(this.activity.getAttendees().size()));
			// Sends message to the application
			this.getAgent().send(jsonMessage);
			return true;
		} catch (NotSerializableException e) {
			e.printStackTrace();
			return false;
		}
	}

	/** Asks this agent user's its confirmation
	 * @param slot 
	 * @return success 
	 */
	private boolean askConfirmation(Slot<T> slot) {
		try {
			JSONMessage jsonMessage = new JSONMessage();
			// Sender : this agent
			jsonMessage.setField(Fields.SENDER, this.agent.getName());
			// Command Type
			jsonMessage.setField(Fields.COMMAND, AskValidation.class.getName());
			// Addressee
			jsonMessage.setField(Fields.ADDRESSEES, JSONMessage.localAddressee);
			// Activity
			jsonMessage.setField(Fields.ACTIVITY, Serializer.serialize(this.activity));
			// Slot
			jsonMessage.setField(Fields.SLOT, Serializer.serialize(slot));
			// Sends message to the application, which will send the user a notification
			this.getAgent().send(jsonMessage);
			return true;
		} catch (NotSerializableException e) {
			e.printStackTrace();
			return false;
		}
	}

}
