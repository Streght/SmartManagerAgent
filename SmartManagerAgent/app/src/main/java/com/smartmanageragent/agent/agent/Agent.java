package agent;

import java.util.List;

import commands.Command;
import commands.Invocator;
import message.Message;
import message.MessageQueue;
import timeTable.Activity;
import timeTable.TimeTable;

// Using abstract class : template method
public abstract class Agent<K, T, U> implements Invocator<K, T, U>, Runnable {

	protected String name;
	// State
	protected State<K, T> state;
	// Messages queues
	protected MessageQueue<U> receiving;
	protected MessageQueue<U> sending;
	
	
	public Agent(MessageQueue<U> receiving, MessageQueue<U> sending) {
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
	 */
	public Message<U> receive() {
		return this.receiving.get();
	}
	
	/** Adds a message to the outgoing message queue
	 * @param message
	 */
	public void send(Message<U> message) {
		this.sending.add(message);
	}
	
	/** Waits for messages
	 * @throws InterruptedException 
	 */
	public void waitMessages() throws InterruptedException {
		synchronized (receiving) {
			// TODO : think about using wait(timeout)
			this.receiving.wait();
		}
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
