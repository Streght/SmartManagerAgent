package com.smartmanageragent.smartagent.application;

import com.smartmanageragent.smartagent.commands.Command;
import com.smartmanageragent.smartagent.commands.Invocator;
import com.smartmanageragent.smartagent.message.MessageQueue;

public class Application<K, T, U> implements Invocator<K, T, U>, Runnable {

	// Messages queues
	private MessageQueue<U> receiving;
	private MessageQueue<U> sending;
	
	public Application(MessageQueue<U> receiving, MessageQueue<U> sending) {
		this.receiving = receiving;
		this.sending = sending;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean invoke(Command<K, T, U> command) {
		// TODO Auto-generated method stub
		return false;
	}

}
