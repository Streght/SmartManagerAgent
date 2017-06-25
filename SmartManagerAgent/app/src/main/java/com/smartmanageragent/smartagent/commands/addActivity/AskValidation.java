package com.smartmanageragent.smartagent.commands.addActivity;


import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.commands.Command;
import com.smartmanageragent.smartagent.message.Message;

public class AskValidation<K, T> extends Command<K, T, String> {
	
	public AskValidation(Message<String> message, Agent<K, T, String> agent) {
		super(message, agent);
		try {
			// TODO Auto-generated constructor stub
			
			// The name of the agent who sent the slot is defined in the "sender" field, although it normally should be
			// this agent name. Explained in SlotReceived.
			
			
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
