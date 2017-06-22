package com.smartmanageragent.smartagent.message;

import java.util.LinkedList;

public class WaitingQueue<T> {

    private LinkedList<Message<T>> queue;
    private long waitingTime = 1800000; // 30 minutes de délai
    private MessageQueue<T> messageQueue;

    /** Synchronized message queue
     */
    public WaitingQueue(MessageQueue<T> mQ) {
        this.queue = new LinkedList<Message<T>>();
        this.messageQueue = mQ;
    }

    /** Adds a message to the queue
     * @param m
     */
    public synchronized void add(Message<T> m) {
        this.queue.add(m);
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

    public long getWaitingTime() {
        return waitingTime;
    }

    public void emptyToMessageQueue () {
        while (!this.queue.isEmpty()) {
            this.messageQueue.add(this.queue.removeFirst());
        }
    }
}
