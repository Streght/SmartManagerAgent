package commands;

import agent.State;
import message.Message;

public class CommandFactory<K, T, U> {

	public CommandFactory() {}
	
	/** Creates a command from a message, using a "command" attribute
	 * @param mess
	 * @param state
	 * @return command
	 */
	@SuppressWarnings("unchecked")
	public Command<K, T, U> createCommand(Message<U> mess, State<K, T> state) {
		Command<K, T, U> command = null;
		// =============================================================================================================
		String className = "commands.agent.AddActivity"; // TODO : get class name from message !!
		// =============================================================================================================
		try {
			// Gets the required command class
			Class<?> cls = Class.forName(className);
			command = (Command<K, T, U>) 
					cls.getDeclaredConstructor(Message.class, State.class).newInstance(mess, state);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return command;
	}
	
}
