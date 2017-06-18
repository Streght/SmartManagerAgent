package agent;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import commands.Command;
import commands.CommandFactory;
import message.Message;
import message.MessageQueue;
import timeTable.Activity;
import timeTable.TimeTable;
import timeTable.TimeTableImpl;
import timeTable.slot.Slot;

public class AgentImpl<T> extends Agent<Date, Float, T> {

	private CommandFactory<Date, Float, T> factory;
	
	public AgentImpl(MessageQueue<T> receiving, MessageQueue<T> sending) {
		super(receiving, sending);
		this.state.setTimeTable(new TimeTableImpl());
		// Factory used to create commands
		this.factory = new CommandFactory<Date, Float, T>();
	}
	
	/** Finds room for an activity in a list of timetables
	 * @param timeTables
	 * @return the position of the activity, null if no place found
	 */
	@Override
	public Object findSlot(List<TimeTable<Date, Float>> timeTables, Activity<Float> act) {
		Object found = null;
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
	private Date recFind(List<TimeTable<Date, Float>> timeTables, Slot<Float> slot, Activity<Float> act) {
		int nbTables = timeTables.size();
		if (nbTables==0) {
			return (Date) slot.getRef();
		} else {
			TimeTable<Date, Float> tt = timeTables.get(0);
			// Iterating until corresponding slots found (or not)
			Iterator<Slot<Float>> it = tt.freeTimeIterator();
			Slot<Float> curSlot, inter;
			int comp = -1;
			while(it.hasNext() && comp<=0) {
				curSlot = it.next();
				comp = slot.compareTo(curSlot);
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
	
	/** Treats messages received
	 */
	private void treatMessages() {
		// TODO : adapt according to Maxime's work
		Message<T> message = this.receiving.get();
		while(message != null) {
			Command<Date, Float, T> command = this.factory.createCommand(message, this.state);
			this.invoke(command);
			message = this.receiving.get();
		}
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				// Treats all messages
				this.treatMessages();
				// Waits for messages (from other agents, user...)
				this.waitMessages();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String toString() {
		return "Name: "+this.name+"\n"+this.state.timeTable.toString();
	}

}
