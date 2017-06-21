package com.smartmanageragent.smartagent.agent;

import java.util.ArrayList;
import java.util.HashMap;

import com.smartmanageragent.smartagent.timeTable.TimeTable;

public class State<K, T> {
	
	protected TimeTable<K, T> timeTable;
	// Activities' names -> names of the persons whose confirmation is awaited
	private HashMap<String, ArrayList<String>> waitingForAddition;
	private HashMap<String, ArrayList<String>> waitingForRemove;
	// TODO : add an attribute to store received time tables !!
	
	// Number of people who accepted/refused a slot for a given activity (name)
	private HashMap<String, Results> results;
	
	State() {
		this.timeTable = null;
		this.waitingForAddition = new HashMap<String, ArrayList<String>>();
		this.waitingForRemove = new HashMap<String, ArrayList<String>>();
		this.results = new HashMap<String, Results>();
	}
	
	/**
	 * @return timeTable
	 */
	public TimeTable<K, T> getTimeTable() {
		return this.timeTable;
	}

	/**
	 * @param timeTable
	 */
	public void setTimeTable(TimeTable<K, T> timeTable) {
		this.timeTable = timeTable;
	}
	
	/** Returns true if the confirmation of a person is awaited for a given activity
	 * @param map
	 * @param actName
	 * @param persName
	 * @return isWaitingFor
	 */
	private boolean waitingFor(HashMap<String, ArrayList<String>> map, String actName, String persName) {
		ArrayList<String> list = map.get(actName);
		if (list != null && list.contains(persName)) {
			return true;
		}
		return false;
	}
	
	/**
	 * @param actName
	 * @param persName
	 * @return isWaitingForAddition
	 */
	public boolean waitingForAddition(String actName, String persName) {
		return this.waitingFor(this.waitingForAddition, actName, persName);
	}
	
	/**
	 * @param actName
	 * @param persName
	 * @return isWaitingForRemoval
	 */
	public boolean waitingForRemoval(String actName, String persName) {
		return this.waitingFor(this.waitingForRemove, actName, persName);
	}
	
	/** Removes a person from the list of awaited persons
	 * @param map
	 * @param actName
	 * @param persName
	 * @return number of remaining awaited persons for the given activity, -1 if not found
	 */
	private int removeFromMap(HashMap<String, ArrayList<String>> map, String actName, String persName) {
		int removed = -1;
		ArrayList<String> list = map.get(actName);
		if (list != null) {
			if (list.remove(persName)) {
				removed = list.size();
			}
			// If nobody is awaited for a given activity anymore
			if (removed==0) {
				map.remove(actName);
			}
		}
		return removed;
	}
	
	/**
	 * @param actName
	 * @param persName
	 * @return removed
	 */
	public int removeFromAwaiting(String actName, String persName) {
		int removed = this.removeFromMap(this.waitingForAddition, actName, persName);
		if (removed==-1) {
			removed = this.removeFromMap(this.waitingForRemove, actName, persName);
		}
		return removed;
	}
	
	/** Returns true if the slot proposed for an activity has been accepted
	 * @param actName
	 * @return accepted
	 */
	public boolean accepted(String actName) {
		Results res = this.results.get(actName);
		if (res!=null && res.accepted()) {
			return true;
		}
		return false;
	}
	
	/** Returns true if the slot proposed for an activity has been refused
	 * @param actName
	 * @return refused
	 */
	public boolean refused(String actName) {
		Results res = this.results.get(actName);
		if (res!=null && res.refused()) {
			return true;
		}
		return false;
	}
	
	private class Results {
		int expected;
		int acceptances;
		int refusals;
		protected Results(int exp, int acc, int ref) {
			this.expected = exp;
			this.acceptances = acc;
			this.refusals = ref;
		}
		boolean finished() {
			return this.expected == this.acceptances+this.refusals;
		}
		boolean accepted() {
			return this.finished() && this.acceptances==this.expected;
		}
		boolean refused() {
			return this.finished() && this.refusals>0;
		}
	}
	
}
