package com.smartmanageragent.smartagent.message;

public abstract class Message<T> {
	
	protected T content;
	
	public Message(T content) {
		this.content = content;
	}
	
	/**
	 * @return content
	 */
	public T getContent() {
		return this.content;
	}
	
	/**
	 * @param content
	 */
	public void setContent(T content) {
		this.content = content;
	}
	
	/** Name of the command joined in the message
	 * @return commandName
	 */
	public abstract String getCommandName();
	
	@Override
	public String toString() {
		return this.content.toString();
	}
	
}
