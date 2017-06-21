package com.smartmanageragent.smartagent.timeTable;

import java.io.Serializable;
import java.util.Iterator;

import com.smartmanageragent.smartagent.timeTable.slot.Slot;

public interface TimeTable<K, T> extends Serializable {

	/** Adds an activity at a given position in the time table
	 * @param act
	 * @param pos
	 * @return previous activity associated to the position
	 */
	public Activity<T> addActivity(K pos, Activity<T> act);
	
	/** Removes an activity at a given position in the time table
	 * @param pos
	 * @return previous activity associated to the position
	 */
	public Activity<T> removeActivity(K pos);
	
	/** Removes the first occurrence of an activity
	 * @param act
	 * @return success
	 */
	public boolean removeActivity(Activity<T> act);
	
	/** Returns the activity at the given position
	 * @param pos
	 * @return activity
	 */
	public Activity<T> getActivity(K pos);
	
	/** Adds a free time slot
	 * @param slot
	 * @return success
	 */
	public boolean addFreeTime(Slot<T> slot);
	
	/** Removes a free time slot
	 * @param slot
	 * @return success
	 */
	public boolean removeFreeTime(Slot<T> slot);
	
	/** Iterates over the activities
	 * @return iterator
	 */
	public Iterator<PosAct<K, T>> activityIterator();
	
	/** Iterates over the free slots
	 * @return iterator
	 */
	public Iterator<Slot<T>> freeTimeIterator();
	
	/** An activity and its current position
	 * @param <T>
	 */
	public class PosAct<K, T> {
		public K pos;
		public Activity<T> act;
		public PosAct(K pos, Activity<T> act) {
			this.pos = pos; this.act = act;
		}
		@Override
		public String toString() {
			return this.pos.toString()+" -> "+this.act.toString();
		}
	}
	
	
	
}
