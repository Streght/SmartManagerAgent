package com.smartmanageragent.smartagent;


import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.agent.AgentImpl;
import com.smartmanageragent.smartagent.commands.addActivity.AcceptSlot;
import com.smartmanageragent.smartagent.commands.addActivity.AddActivity;
import com.smartmanageragent.smartagent.message.JSONMessage;
import com.smartmanageragent.smartagent.message.JSONMessage.Fields;
import com.smartmanageragent.smartagent.message.MessageQueue;
import com.smartmanageragent.smartagent.message.Serializer;
import com.smartmanageragent.smartagent.timeTable.Activity;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.util.Date;

public class TestAddActivity {

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
			// Runs some tests
			testAgents();
			// Displays the agents states
			showState();
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
		Date beg = new Date(0);
		Date end = new Date(10000);
		a1 = new AgentImpl<String>("a1", receiveA1, sendA1, beg, end);
		a2 = new AgentImpl<String>("a2", receiveA2, sendA2, beg, end);
		a3 = new AgentImpl<String>("a3", receiveA3, sendA3, beg, end);
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
		act.addAttendee("a1");
		act.addAttendee("a2");
		act.addAttendee("a3");
		// =============================================================================================================
		JSONMessage addAct = new JSONMessage();
		addAct.setField(Fields.ACTIVITY, Serializer.serialize(act));
		addAct.setField(Fields.COMMAND, AddActivity.class.getName());
		// [1] AddActivity
		receiveA1.add(addAct);
		JSONMessage ttReq = (JSONMessage) sendA1.get();
		System.out.println(ttReq);
		// =============================================================================================================
		// [2] TTRequest
		receiveA2.add(ttReq);
		receiveA3.add(ttReq);
		JSONMessage ttAns_a1 = (JSONMessage) sendA2.get();
		JSONMessage ttAns_a2 = (JSONMessage) sendA3.get();
		System.out.println(ttAns_a1);
		System.out.println(ttAns_a2);
		// =============================================================================================================
		// [3] TTAnswer
		receiveA1.add(ttAns_a1);
		receiveA1.add(ttAns_a2);
		JSONMessage slotRec = (JSONMessage) sendA1.get();
		JSONMessage askVal_a1 = (JSONMessage) sendA1.get();
		System.out.println(slotRec);
		System.out.println(askVal_a1);
		// =============================================================================================================
		JSONMessage notAcc = new JSONMessage();
		String strSlot = slotRec.getField(Fields.SLOT);
		Object slot = Serializer.deserialize(strSlot);
		String strAct = slotRec.getField(Fields.ACTIVITY);
		Object activity = Serializer.deserialize(strAct);
		notAcc.setField(Fields.ADDRESSEES, "a1");
		notAcc.setField(Fields.ACTIVITY, Serializer.serialize((Serializable) activity));
		notAcc.setField(Fields.COMMAND, AcceptSlot.class.getName());
		notAcc.setField(Fields.SLOT, Serializer.serialize((Serializable) slot));
		// [4] SlotReceived - NA
		receiveA1.add(notAcc);
		receiveA2.add(slotRec);
		receiveA3.add(slotRec);
		JSONMessage slotAcc_a1 = (JSONMessage) sendA1.get();
		JSONMessage askVal_a2 = (JSONMessage) sendA2.get();
		JSONMessage askVal_a3 = (JSONMessage) sendA3.get();
		System.out.println(slotAcc_a1);
		System.out.println(askVal_a2);
		System.out.println(askVal_a3);
		// =============================================================================================================
		// [5] NotifyAccept - SA
		receiveA1.add(slotAcc_a1);
		receiveA2.add(notAcc);
		receiveA3.add(notAcc);
		JSONMessage slotAcc_a2 = (JSONMessage) sendA2.get();
		JSONMessage slotAcc_a3 = (JSONMessage) sendA3.get();
		System.out.println(slotAcc_a2);
		System.out.println(slotAcc_a3);
		// =============================================================================================================
		// [6] SlotAccepted - CS
		receiveA1.add(slotAcc_a2);
		receiveA1.add(slotAcc_a3);
		JSONMessage confSlot = (JSONMessage) sendA1.get();
		System.out.println(confSlot);
		// =============================================================================================================
		// [7] ConfirmSlot
		receiveA1.add(confSlot);
		receiveA2.add(confSlot);
		receiveA3.add(confSlot);
		// =============================================================================================================
	}
	
	/** Displaying
	 */
	private static void showState() {
		try {Thread.sleep(10);} catch (InterruptedException e) {}
		System.out.println("=========================================================================================");
		System.out.println(a1.toString());
		System.out.println("=========================================================================================");
		System.out.println(a2.toString());
		System.out.println("=========================================================================================");
		System.out.println(a3.toString());
		System.out.println("=========================================================================================");
	}
	
}
