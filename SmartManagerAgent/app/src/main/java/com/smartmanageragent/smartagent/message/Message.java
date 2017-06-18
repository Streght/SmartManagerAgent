package com.smartmanageragent.smartagent.message;

// TODO : use Maxime's fabulous work
public class Message<T> {
	
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
	
	@Override
	public String toString() {
		return this.content.toString();
	}
	
}
