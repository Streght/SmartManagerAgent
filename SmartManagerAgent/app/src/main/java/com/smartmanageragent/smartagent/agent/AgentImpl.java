package com.smartmanageragent.smartagent.agent;


import com.smartmanageragent.smartagent.commands.Command;
import com.smartmanageragent.smartagent.message.Message;
import com.smartmanageragent.smartagent.message.MessageQueue;
import com.smartmanageragent.smartagent.timeTable.Activity;
import com.smartmanageragent.smartagent.timeTable.TimeTable;
import com.smartmanageragent.smartagent.timeTable.TimeTableImpl;
import com.smartmanageragent.smartagent.timeTable.slot.Slot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class AgentImpl<T> extends Agent<Date, Float, T> {
	
	/**
	 * @param name
	 * @param receiving
	 * @param sending
	 */
	public AgentImpl(String name, MessageQueue<T> receiving, MessageQueue<T> sending) {
		super(name, receiving, sending);
		this.state.setTimeTable(new TimeTableImpl());
	}
	
	/**
	 * @param name
	 * @param receiving
	 * @param sending
	 * @param beg
	 * @param end
	 */
	public AgentImpl(String name, MessageQueue<T> receiving, MessageQueue<T> sending, Date beg, Date end) {
		super(name, receiving, sending);
		this.state.setTimeTable(new TimeTableImpl(beg, end));
	}
	
	/** Finds room for an activity in a list of timetables
	 * @param timeTables
	 * @return the position of the activity, null if no place found
	 */
	@Override
	public Slot<Float> findSlot(List<TimeTable<Date, Float>> timeTables, Activity<Float> act) {
		Slot<Float> found = null;
		Slot<Float> curSlot;
		Iterator<Slot<Float>> it = this.state.timeTable.freeTimeIterator();
		while(it.hasNext() && found==null) {
			curSlot = it.next();
			found = this.recFind(timeTables, curSlot, act);
		}
		return found;
	}
	
	/** Recursive finding function
	 * @param timeTables
	 * @param slot
	 * @param act
	 * @return recFInd
	 */
	private Slot<Float> recFind(List<TimeTable<Date, Float>> timeTables, Slot<Float> slot, Activity<Float> act) {
		int nbTables = timeTables.size();
		if (nbTables==0) {
			return slot;
		} else {
			TimeTable<Date, Float> tt = timeTables.get(0);
			// Iterating until corresponding slots found (or not)
			Iterator<Slot<Float>> it = tt.freeTimeIterator();
			Slot<Float> curSlot, inter;
			int comp = -1;
			while(it.hasNext() && comp<=0) {
				curSlot = it.next();
				if (curSlot == null)
					return null;
				comp = curSlot.compareTo(slot);
				// If the two slots are crossing
				if (comp==0) {
					inter = slot.intersection(curSlot);
					if (inter!=null && inter.fits(act.getLength())) {
						// Copies the list to pass it to the recursive function
						List<TimeTable<Date, Float>> listCopy = new ArrayList<TimeTable<Date, Float>>(timeTables);
						listCopy.remove(0);
						return recFind(listCopy, inter, act);
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				// Treats incoming messages
				Message<T> message = this.receiving.get();
				System.out.println(this.name+" received : "+message);
				Command<Date, Float, T> command = this.factory.createCommand(message, this);
				this.invoke(command);
				/* 
				 * TODO : use timeout
				 * To avoid waiting infinitely for a special message, send the agent a canceling message after an amount
				 * of time to wake it up. You must include a command that will change the internal state of the agent so
				 * it's not waiting anymore for that special message.
				 * You should launch that timer IN A COMMAND, following the sample below :
				 *   Message<U> mess = null;
				 *   TimeOut<U> to = new TimeOut<U>(this.receiving, mess, timeOut);
				 *   to.runTimer();
				 */
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String toString() {
		return this.name+"\n"+this.state.toString();
	}

}
