package com.smartmanageragent.smartagent.agent;


import com.smartmanageragent.smartagent.timeTable.TimeTable;

import java.util.HashMap;

public class State<K, T> {
	
	// Time table of the agent
	protected TimeTable<K, T> timeTable;
	// List of attributes used by commands
	protected HashMap<String, Object> attributes;
	
	public State() {
		this.timeTable = null;
		this.attributes = new HashMap<String, Object>();
	}
	
	/**
	 * @return timeTable
	 */
	public synchronized TimeTable<K, T> getTimeTable() {
		return this.timeTable;
	}

	/**
	 * @param timeTable
	 */
	public synchronized void setTimeTable(TimeTable<K, T> timeTable) {
		this.timeTable = timeTable;
	}
	
	/** Adds/replaces an attribute to the internal state
	 * @param name
	 * @param value
	 * @return true if there was already an attribute with the given name, else false
	 */
	public synchronized boolean addAttribute(String name, Object value) {
		return this.attributes.put(name, value)!=null;
	}
	
	/** Removes an attribute from the internal state
	 * @param name
	 * @return true if the attribute was existing, else false
	 */
	public synchronized boolean removeAttribute(String name) {
		return this.attributes.remove(name)!=null;
	}

	/** Returns the value of an attribute, from its name
	 * @param name
	 * @return the attribute value, null if not existing
	 */
	public synchronized Object getAttribute(String name) {
		return this.attributes.get(name);
	}
	
	/** Clears all the attributes of the internal state
	 */
	public synchronized void clear() {
		this.attributes.clear();
	}
	
	@Override
	public synchronized String toString() {
		return "=> Att : \n"+this.attributes.toString()+"\n=> TT : \n"+this.timeTable.toString();
	}
	
}
