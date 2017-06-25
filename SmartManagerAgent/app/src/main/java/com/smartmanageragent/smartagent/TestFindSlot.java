package com.smartmanageragent.smartagent;


import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.agent.AgentImpl;
import com.smartmanageragent.smartagent.message.MessageQueue;
import com.smartmanageragent.smartagent.timeTable.Activity;
import com.smartmanageragent.smartagent.timeTable.TimeTable;
import com.smartmanageragent.smartagent.timeTable.slot.Slot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class TestFindSlot {

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
		// Creates message queues and agents
		buildAgents();
		// Building time tables
		buildTT();
		// Testing time slot algorithm
		testFindSlot();
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

	/** Builds up time tables
	 */
	private static void buildTT() {
		// =============================================================================================================
		a1.insert(new Date(0), new Activity<Float>((float) 1000, 2, "tt1_act1"));
		a1.insert(new Date(1500), new Activity<Float>((float) 500, 2, "tt1_act2"));
		a1.insert(new Date(4000), new Activity<Float>((float) 2000, 2, "tt1_act3"));
		a1.insert(new Date(9000), new Activity<Float>((float) 1000, 2, "tt1_act4"));
		Iterator<Slot<Float>> ftIt = a1.getTimeTable().freeTimeIterator();
		while(ftIt.hasNext()) {
			Slot<Float> slot = ftIt.next();
			System.out.println(slot);
		}
		// =============================================================================================================
		a2.insert(new Date(3000), new Activity<Float>((float) 500, 2, "tt2_act1"));
		a2.insert(new Date(4000), new Activity<Float>((float) 500, 2, "tt2_act2"));
		a2.insert(new Date(5000), new Activity<Float>((float) 5000, 2, "tt2_act3"));
		ftIt = a2.getTimeTable().freeTimeIterator();
		while(ftIt.hasNext()) {
			Slot<Float> slot = ftIt.next();
			System.out.println(slot);
		}
		// =============================================================================================================
		a3.insert(new Date(500), new Activity<Float>((float) 2000, 2, "tt3_act1"));
		a3.insert(new Date(6000), new Activity<Float>((float) 1000, 2, "tt3_act1"));
		a3.insert(new Date(8000), new Activity<Float>((float) 1000, 2, "tt3_act1"));
		ftIt = a3.getTimeTable().freeTimeIterator();
		while(ftIt.hasNext()) {
			Slot<Float> slot = ftIt.next();
			System.out.println(slot);
		}
		// =============================================================================================================
	}
	
	/** Testing find slot algorithm
	 */
	private static void testFindSlot() {
		System.out.println("=========================================================================================");
		Activity<Float> act1 = new Activity<Float>((float) 500, 2, "act1");
		Object slot1_a1 = findSlotA1(act1);
		System.out.println(slot1_a1);
		System.out.println("=========================================================================================");
		Object slot1_a2 = findSlotA2(act1);
		System.out.println(slot1_a2);
		System.out.println("=========================================================================================");
		Object slot1_a3 = findSlotA3(act1);
		System.out.println(slot1_a3);
		System.out.println("=========================================================================================");
	}
	
	/**
	 * @param act
	 * @return slot
	 */
	private static Object findSlotA1(Activity<Float> act) {
		List<TimeTable<Date, Float>> timeTables = new ArrayList<TimeTable<Date, Float>>();
		timeTables.add(a2.getTimeTable());
		timeTables.add(a3.getTimeTable());
		Object slot = a1.findSlot(timeTables, act);
		return slot;
	}

	/**
	 * @param act
	 * @return slot
	 */
	private static Object findSlotA2(Activity<Float> act) {
		List<TimeTable<Date, Float>> timeTables = new ArrayList<TimeTable<Date, Float>>();
		timeTables.add(a1.getTimeTable());
		timeTables.add(a3.getTimeTable());
		Object slot = a2.findSlot(timeTables, act);
		return slot;
	}
	
	/**
	 * @param act
	 * @return slot
	 */
	private static Object findSlotA3(Activity<Float> act) {
		List<TimeTable<Date, Float>> timeTables = new ArrayList<TimeTable<Date, Float>>();
		timeTables.add(a1.getTimeTable());
		timeTables.add(a2.getTimeTable());
		Object slot = a3.findSlot(timeTables, act);
		return slot;
	}

}
