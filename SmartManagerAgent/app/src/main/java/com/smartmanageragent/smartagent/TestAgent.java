package com.smartmanageragent.smartagent;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.Date;

import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.agent.AgentImpl;
import com.smartmanageragent.smartagent.commands.list.AddActivity;
import com.smartmanageragent.smartagent.commands.list.NotifyAccept;
import com.smartmanageragent.smartagent.message.JSONMessage;
import com.smartmanageragent.smartagent.message.JSONMessage.Fields;
import com.smartmanageragent.smartagent.message.MessageQueue;
import com.smartmanageragent.smartagent.message.Serializer;
import com.smartmanageragent.smartagent.timeTable.Activity;
import com.smartmanageragent.smartagent.timeTable.TimeTable;
import com.smartmanageragent.smartagent.timeTable.TimeTableImpl;

public class TestAgent {

	// Agents message queues
	private static MessageQueue<String> receiveA1;
	private static MessageQueue<String> sendA1;
	private static MessageQueue<String> receiveA2;
	private static MessageQueue<String> sendA2;
	private static MessageQueue<String> receiveA3;
	private static MessageQueue<String> sendA3;
	// Agents
	private static Agent<Date, Float, String> a1;
	private static Agent<Date, Float, String> a2;
	private static Agent<Date, Float, String> a3;
	
	public static void main(String[] args) {
		try {
			// Creates message queues and agents
			buildAgents();
			// Sets the content of the agents time tables
			// TODO : add content to the TT to test findSlot efficiency !!!
			setTimeTables();
			// Runs some tests
			testAgents();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/** Building
	 */
	private static void buildAgents() {
		// Message queues
		receiveA1 = new MessageQueue<String>();
		sendA1 = new MessageQueue<String>();
		receiveA2 = new MessageQueue<String>();
		sendA2 = new MessageQueue<String>();
		receiveA3 = new MessageQueue<String>();
		sendA3 = new MessageQueue<String>();
		// Agents
		a1 = new AgentImpl<String>("a1", receiveA1, sendA1);
		a2 = new AgentImpl<String>("a2", receiveA2, sendA2);
		a3 = new AgentImpl<String>("a3", receiveA3, sendA3);
	}

	public static void setTimeTables() {
		// Tool values
		Activity<Float> empty = new Activity<Float>((float) 1, TimeTableImpl.unPriority, TimeTableImpl.unName);
		Date beg = new Date(0);
		Date end = new Date(10000);
		// ====================================================> A1 <===================================================
		TimeTable<Date, Float> ttA1 = a1.getTimeTable();
		ttA1.addActivity(beg, empty);
		ttA1.addActivity(end, empty);
		// ====================================================> A2 <===================================================
		TimeTable<Date, Float> ttA2 = a2.getTimeTable();
		ttA2.addActivity(beg, empty);
		ttA2.addActivity(end, empty);
		// ====================================================> A3 <===================================================
		TimeTable<Date, Float> ttA3 = a3.getTimeTable();
		ttA3.addActivity(beg, empty);
		ttA3.addActivity(end, empty);
	}
	
	/** Testing
	 * @throws NotSerializableException 
	 * @throws InterruptedException 
	 */
	private static void testAgents() throws NotSerializableException, InterruptedException {
		// Launches the agents in different threads
		new Thread(a1).start();
		new Thread(a2).start();
		new Thread(a3).start();
		Thread.sleep(100);
		// ============================================================================================================
		Activity<Float> act = new Activity<Float>((float) 5000, 3, "exam");
		act.addAttendee("a2");
		act.addAttendee("a3");
		// =============================================================================================================
		JSONMessage m1 = new JSONMessage();
		m1.setField(Fields.ACTIVITY, Serializer.serialize(act));
		m1.setField(Fields.COMMAND, AddActivity.class.getName());
		// AddActivity
		receiveA1.add(m1);
		JSONMessage m2 = (JSONMessage) sendA1.get();
		System.out.println(m2);
		// =============================================================================================================
		// TTRequest
		receiveA2.add(m2);
		receiveA3.add(m2);
		JSONMessage m3_a2 = (JSONMessage) sendA2.get();
		JSONMessage m3_a3 = (JSONMessage) sendA3.get();
		System.out.println(m3_a2);
		System.out.println(m3_a3);
		// =============================================================================================================
		// TTAnswer
		receiveA1.add(m3_a2);
		receiveA1.add(m3_a3);
		JSONMessage m4 = (JSONMessage) sendA1.get();
		System.out.println(m4);
		// =============================================================================================================
		// SlotReceived
		receiveA2.add(m4);
		receiveA3.add(m4);
		JSONMessage m5_a2 = (JSONMessage) sendA2.get();
		JSONMessage m5_a3 = (JSONMessage) sendA3.get();
		System.out.println(m5_a2);
		System.out.println(m5_a3);
		// =============================================================================================================
		JSONMessage m6 = new JSONMessage();
		m6.setField(Fields.ADDRESSEES, "a1");
		String strSlot = m4.getField(Fields.SLOT);
		Object slot = Serializer.deserialize(strSlot);
		m6.setField(Fields.SLOT, Serializer.serialize((Serializable) slot));
		String strAct = m4.getField(Fields.ACTIVITY);
		Object activity = Serializer.deserialize(strAct);
		m6.setField(Fields.ACTIVITY, Serializer.serialize((Serializable) activity));
		m6.setField(Fields.COMMAND, NotifyAccept.class.getName());
		// NotifyAccept
		receiveA2.add(m6);
		receiveA3.add(m6);
		JSONMessage m7_a2 = (JSONMessage) sendA2.get();
		JSONMessage m7_a3 = (JSONMessage) sendA3.get();
		System.out.println(m7_a2);
		System.out.println(m7_a3);
		// =============================================================================================================
		// SlotAccepted
		receiveA1.add(m7_a2);
		receiveA1.add(m7_a3);
		JSONMessage m8 = (JSONMessage) sendA1.get();
		System.out.println(m8);
		// =============================================================================================================
	}
	
}
