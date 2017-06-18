package com.smartmanageragent.smartagent.commands;

import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.message.Message;

public abstract class Command<K, T, U> {
	
	protected Agent<K, T, U> agent;
	
	/** Default constructor
	 * @param message
	 * @param state
	 */
	public Command(Message<U> message, Agent<K, T, U> agent) {
		this.agent = agent;
	}
	
	/**
	 * @return agent
	 */
	public Agent<K, T, U> getAgent() {
		return this.agent;
	}

	/**
	 * @param agent
	 */
	public void setAgent(Agent<K, T, U> agent) {
		this.agent = agent;
	}

	/** Executes the command
	 * @return success
	 */
	public abstract boolean execute();
	
}
