package com.smartmanageragent.smartagent.commands.removeActivity;


import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.commands.Command;
import com.smartmanageragent.smartagent.message.Message;

public class RemoveRefused<K, T> extends Command<K, T, String> {
	
	public RemoveRefused(Message<String> message, Agent<K, T, String> agent) {
		super(message, agent);
		try {
			// TODO Auto-generated constructor stub
		} catch (Exception e) {
			e.printStackTrace();
			this.buildError = true;
		}
	}

	@Override
	public boolean execute() {
		if (this.buildError)
			return false;
		// TODO Auto-generated method stub
		return true;
	}

}
