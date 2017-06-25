package com.smartmanageragent.smartagent.commands;


import com.smartmanageragent.smartagent.agent.Agent;
import com.smartmanageragent.smartagent.message.Message;

public class CommandFactory<K, T, U> {

	public CommandFactory() {}
	
	/** Creates a command from a message, using a "command" attribute
	 * @param mess
	 * @param state
	 * @return command
	 */
	@SuppressWarnings("unchecked")
	public Command<K, T, U> createCommand(Message<U> mess, Agent<K, T, U> agent) {
		Command<K, T, U> command = null;
		try {
			// Name of the command
			String className = mess.getCommandName();
			// Gets the required command class
			Class<?> cls = Class.forName(className);
			command = (Command<K, T, U>)cls.getDeclaredConstructor(Message.class, Agent.class).newInstance(mess, agent);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return command;
	}
	
}
