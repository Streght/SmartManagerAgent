package commands.list;

import agent.Agent;

import commands.Command;
import message.Message;

public class TTRequest<K, T> extends Command<K, T, String> {

	public TTRequest(Message<String> message, Agent<K, T, String> agent) {
		super(message, agent);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean execute() {
		// TODO Auto-generated method stub
		return false;
	}

}
