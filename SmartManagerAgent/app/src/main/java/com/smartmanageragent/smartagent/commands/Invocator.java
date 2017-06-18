package com.smartmanageragent.smartagent.commands;

public interface Invocator<K, T, U> {

	/** Invokes the 'execute' method of a command
	 * @return success
	 */
	public boolean invoke(Command<K, T, U> command);
	
}
