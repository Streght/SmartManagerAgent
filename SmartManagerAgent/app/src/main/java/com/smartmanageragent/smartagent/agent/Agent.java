package com.smartmanageragent.smartagent.agent;

import java.util.List;

import com.smartmanageragent.smartagent.commands.Command;
import com.smartmanageragent.smartagent.commands.Invocator;
import com.smartmanageragent.smartagent.message.Message;
import com.smartmanageragent.smartagent.message.MessageQueue;
import com.smartmanageragent.smartagent.timeTable.Activity;
import com.smartmanageragent.smartagent.timeTable.TimeTable;

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
	
	
	public Agent(String name, MessageQueue<U> receiving, MessageQueue<U> sending) {
		this.name = name;
		this.state = new State<K, T>();
		this.receiving = receiving;
		this.sending = sending;
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
	
	/** Finds room for an activity in a list of timetables
	 * @param timeTables
	 * @return the position of the activity, null if no place found
	 */
	public abstract Object findSlot(List<TimeTable<K, T>> timeTables, Activity<T> act);
	
	@Override
	public boolean invoke(Command<K, T, U> command) {
		return command.execute();
	}
	
}
