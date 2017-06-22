package com.smartmanageragent.smartagent.message;

import java.io.Serializable;
import java.util.LinkedList;

public class MessageQueue<T> implements Serializable {

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
		this.notifyAll();
	}
	
	/** Removes the first message from the list
	 * @return older message, null if none
	 * @throws InterruptedException
	 */
	public synchronized Message<T> get() throws InterruptedException {
		while (this.queue.isEmpty()) {
			this.wait();
		}
		return this.queue.remove();
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		int size = this.queue.size();
		for (int i=0; i<size; i++) {
			buffer.append("# "+this.queue.get(i).toString()+"\n");
		}
		return buffer.toString();
	}
	
}
