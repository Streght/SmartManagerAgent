package com.smartmanageragent.smartagent.message;

import java.util.LinkedList;

public class MessageQueue<T> {

	private LinkedList<Message<T>> queue;
	
	/** Synchronized message queue
	 */
	public MessageQueue() {
		this.queue = new LinkedList<Message<T>>();
	}
	
	/** Adds a message to the queue
	 * @param m
	 */
	public synchronized void add(Message<T> m) {
		this.queue.add(m);
		// Notifies all the people using the message Queue
		synchronized (this) {
			this.notifyAll();
		}
	}
	
	/** Removes the first message from the list
	 * @return older message, null if none
	 */
	public synchronized Message<T> get() {
		if (this.queue.size() > 0)
			return this.queue.remove();
		return null;
	}

	@Override
	public synchronized String toString() {
		StringBuffer buffer = new StringBuffer();
		int size = this.queue.size();
		for (int i=0; i<size; i++) {
			buffer.append("# "+this.queue.get(i).toString()+"\n");
		}
		return buffer.toString();
	}
	
}
