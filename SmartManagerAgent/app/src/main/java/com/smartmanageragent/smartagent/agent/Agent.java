package com.smartmanageragent.smartagent.agent;


import com.smartmanageragent.smartagent.commands.Command;
import com.smartmanageragent.smartagent.commands.CommandFactory;
import com.smartmanageragent.smartagent.commands.Invocator;
import com.smartmanageragent.smartagent.message.Message;
import com.smartmanageragent.smartagent.message.MessageQueue;
import com.smartmanageragent.smartagent.timeTable.Activity;
import com.smartmanageragent.smartagent.timeTable.TimeTable;
import com.smartmanageragent.smartagent.timeTable.slot.Slot;

import java.util.ArrayList;
import java.util.List;

// Using abstract class : template method
public abstract class Agent<K, T, U> implements Invocator<K, T, U>, Runnable {

	// Time before canceling an operation
	@SuppressWarnings("unused")
	private static final int timeOut = 60;
	// Agent name
	protected String name;
	// State
	protected State<K, T> state;
	// Messages queues
	protected MessageQueue<U> receiving;
	protected MessageQueue<U> sending;
	// Command factory
	protected CommandFactory<K, T, U> factory;
	
	public Agent(String name, MessageQueue<U> receiving, MessageQueue<U> sending) {
		this.name = name;
		this.state = new State<K, T>();
		this.receiving = receiving;
		this.sending = sending;
		this.factory = new CommandFactory<K, T, U>();
	}
	
	/**
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return state
	 */
	public State<K, T> getState() {
		return this.state;
	}
	
	/**
	 * @return timeTable
	 */
	public TimeTable<K, T> getTimeTable() {
		return this.state.timeTable;
	}
	
	/**
	 * @param timeTable
	 */
	public void setTimeTable(TimeTable<K, T> timeTable) {
		this.state.timeTable = timeTable;
	}
	
	/**
	 * @return receiving
	 */
	public MessageQueue<U> getReceiving() {
		return this.receiving;
	}

	/**
	 * @param receiving
	 */
	public void setReceiving(MessageQueue<U> receiving) {
		this.receiving = receiving;
	}

	/**
	 * @return sending
	 */
	public MessageQueue<U> getSending() {
		return this.sending;
	}

	/**
	 * @param sending
	 */
	public void setSending(MessageQueue<U> sending) {
		this.sending = sending;
	}
	
	/** Gets a message from the incoming message queue
	 * @return message
	 * @throws InterruptedException 
	 */
	public Message<U> receive() throws InterruptedException {
		return this.receiving.get();
	}

	/** Adds a message to the outgoing message queue
	 * @param message
	 */
	public void send(Message<U> message) {
		this.sending.add(message);
	}
	
	/** Adds an activity at a given position in the time table
	 * @param pos
	 * @param act
	 * @return previous activity associated to the position
	 */
	public Activity<T> insert(K pos, Activity<T> act) {
		return this.state.timeTable.addActivity(pos, act);
	}
	
	/** Removes an activity at a given position in the time table
	 * @param pos
	 * @return previous activity associated to the position
	 */
	public Activity<T> remove(K pos) {
		return this.state.timeTable.removeActivity(pos);
	}
	
	/** Returns the attendees list of an activity, this agent excepted
	 * @param act
	 * @return otherAttendees
	 */
	public List<String> otherAttendees(Activity<T> act) {
		List<String> otherAttendees = new ArrayList<String>(act.getAttendees());
		otherAttendees.remove(this.name);
		return otherAttendees;
	}
	
	/** Finds room for an activity in a list of timetables
	 * @param timeTables
	 * @return the position of the activity, null if no place found
	 */
	public abstract Slot<T> findSlot(List<TimeTable<K, T>> timeTables, Activity<T> act);
	
	@Override
	public boolean invoke(Command<K, T, U> command) {
		return command.execute();
	}
	
}
