package com.smartmanageragent.smartagent.timeTable;

import java.util.ArrayList;
import java.util.List;

public class Activity<T> {

	private T length;
	private int priority;
	private String name;
	private List<String> attendees;
	
	public Activity(T length, int priority, String name) {
		this.length = length;
		this.priority = priority;
		this.name = name;
		this.attendees = new ArrayList<String>();
	}

	/**
	 * @return length
	 */
	public T getLength() {
		return this.length;
	}

	/**
	 * @param length
	 */
	public void setLength(T length) {
		this.length = length;
	}

	/**
	 * @return priority
	 */
	public int getPriority() {
		return this.priority;
	}

	/**
	 * @param priority
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return attendees
	 */
	public List<String> getAttendees() {
		return this.attendees;
	}
	
	/** Adds an attendee to the meeting
	 * @param att
	 */
	public void addAttendee(String att) {
		this.attendees.add(att);
	}
	
	/** Removes an attendee from the meeting
	 * @param att
	 * @return true if the attendee has been removed
	 */
	public boolean removeAttendee(String att) {
		return this.attendees.remove(att);
	}
	
	@Override
	public boolean equals(Object obj) {
		@SuppressWarnings("unchecked")
		Activity<T> act = (Activity<T>) obj;
		return this.length.equals(act.length) && this.priority==act.priority && this.name.equals(act.name);
	}

	@Override
	public String toString() {
		return "("+this.priority+") "+this.name+": "+this.length.toString();
	}
	
}
