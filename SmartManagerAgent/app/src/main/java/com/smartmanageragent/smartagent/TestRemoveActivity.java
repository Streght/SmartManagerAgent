package com.smartmanageragent.smartagent;

import java.io.NotSerializableException;
import java.util.Date;

import agent.Agent;
import agent.AgentImpl;
import message.MessageQueue;
import timeTable.Activity;

public class TestRemoveActivity {

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
			// Builds up agents time table
			buildTT();
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
	
	/** Time tables
	 */
	private static void buildTT() {
		Activity<Float> act = new Activity<Float>((float) 2500, 2, "testAct");
		act.addAttendee("a1");
		act.addAttendee("a2");
		act.addAttendee("a3");
		Date begAct = new Date(5000);
		a1.insert(begAct, act);
		a2.insert(begAct, act);
		a3.insert(begAct, act);
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
		// TODO !!
	}
	
	/** Displaying
	 */
	private static void showState() {
		System.out.println("=========================================================================================");
		System.out.println(a1.toString());
		System.out.println("=========================================================================================");
		System.out.println(a2.toString());
		System.out.println("=========================================================================================");
		System.out.println(a3.toString());
		System.out.println("=========================================================================================");
	}
	
}
