package application;

import commands.Command;
import commands.Invocator;
import message.MessageQueue;

public class Application<K, T, U> implements Invocator<K, T, U>, Runnable {

	// Messages queues
	protected MessageQueue<U> receiving;
	protected MessageQueue<U> sending;
	
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
