package com.smartmanageragent.smartagent.agent;

import java.util.Timer;
import java.util.TimerTask;

import com.smartmanageragent.smartagent.message.Message;
import com.smartmanageragent.smartagent.message.MessageQueue;

/** Sends a message after a given waiting time
 * @param <T>
 */
public class TimeOut<T> {
	
	// Queue to send the message into after timeout
	private MessageQueue<T> queue;
	// Message sent after timeout (in seconds)
	private Message<T> message;
	// Time to wait before sending message
	private int waitingTime;
	private Timer timer;
	
	/**
	 * @param queue
	 * @param message
	 * @param waitingTime
	 */
	public TimeOut(MessageQueue<T> queue, Message<T> message, int waitingTime) {
		this.queue = queue;
		this.message = message;
		this.waitingTime = waitingTime;
		this.timer = new Timer();
	}
	
	/** Sends a timeout message in the queue, then clears the timer
	 */
	private void sendMessage() {
		this.queue.add(this.message);
		this.stopTimer();
	}
	
	/** Stops/clears the timer
	 */
	public void stopTimer() {
		this.timer.cancel();
		this.timer.purge();
	}
	
	/** Launches the timer
	 */
	public void runTimer() {
		final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                sendMessage();
            }
        };
		timer.schedule(task, this.waitingTime*1000);
	}
	
}
