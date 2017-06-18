package com.smartmanageragent.smartagent.timeTable;

import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.smartmanageragent.smartagent.timeTable.slot.Slot;
import com.smartmanageragent.smartagent.timeTable.slot.SlotImpl;

public class TimeTableImpl implements TimeTable<Date, Float> {
	
	public static final String unName = "UNAVAILABLE";
	public static final int unPriority = 5;
	public NavigableMap<Date, Activity<Float>> activities;
	
	public TimeTableImpl() {
		this.activities = new TreeMap<Date, Activity<Float>>(new DateComparator());
	}

	// TODO : create TimeTable from JSON object (Google Calendar API)
	//public TimeTableImpl() {}
	
	// TODO : create JSON object from TimeTable (Google Calendar API)
	//public toJSON() {}

	/** Initializes a time table with a list of free slots, ordered
	 * @param freeSlots
	 * @param today
	 * @param last
	 */
	public TimeTableImpl(List<SlotImpl> freeSlots, Date today, Date last) {
		int nbSlots = freeSlots.size();
		Date beg = today, end;
		Float length;
		Activity<Float> act = new Activity<Float>((float) 0, unPriority, unName);
		for (int i=0; i<nbSlots; i++) {
			end = freeSlots.get(i).beg;
			length = (float) (end.getTime() - beg.getTime());
			act.setLength(length);
			beg = freeSlots.get(i).end;
		}
		end = last;
		length = (float) (end.getTime() - beg.getTime());
		act.setLength(length);
		this.addActivity(beg, act);
	}
	
	@Override
	public Activity<Float> addActivity(Date pos, Activity<Float> act) {
		return this.activities.put(pos, act);
	}

	@Override
	public Activity<Float> removeActivity(Date pos) {
		return this.activities.remove(pos);
	}

	@Override
	public boolean removeActivity(Activity<Float> act) {
		return this.activities.values().remove(act);
	}
	

	@Override
	public Activity<Float> getActivity(Date pos) {
		return this.activities.get(pos);
	}
	
	@Override
	public boolean addFreeTime(Slot<Float> slot) {
		SlotImpl sl = (SlotImpl) slot;
		// Assuming free time slot is added at the right place (ie occupied slot)
		Date prevActBeg, prevActEnd;
		Activity<Float> prevAct = this.activities.get((Date) sl.beg);
		if (prevAct!=null) {
			prevActBeg = sl.beg;
		} else {
			Entry<Date, Activity<Float>> prevActEntry = this.activities.lowerEntry((Date) sl.beg);
			prevAct = prevActEntry.getValue();
			if (prevAct == null || prevAct.getName()!=unName) {
				return false;
			}
			prevActBeg = prevActEntry.getKey();
		}
		prevActEnd = new Date((long) (prevActBeg.getTime() + prevAct.getLength()));
		// Removes activity from time table
		this.removeActivity(prevActBeg);
		Activity<Float> before, after;
		// Before the newly added free time slot
		if (!prevActBeg.equals(sl.beg)) {
			before = new Activity<Float>((float) (sl.beg.getTime()-prevActBeg.getTime()), unPriority, unName);
			this.addActivity(prevActBeg, before);
		}
		// After the newly added free time slot
		if (!prevActEnd.equals(sl.end)) {
			after = new Activity<Float>((float) (prevActEnd.getTime()-sl.end.getTime()), unPriority, unName);
			this.addActivity(sl.end, after);
		}
		return true;
	}

	@Override
	public boolean removeFreeTime(Slot<Float> slot) {
		SlotImpl sl = (SlotImpl) slot;
		// Assuming the given slot represents a free time slot
		Date beg = sl.beg;
		Date end = sl.end;
		Date prevActBeg, prevActEnd, nextActBeg;
		Activity<Float> prevAct = this.activities.get(sl.beg);
		if (prevAct != null) {
			prevActBeg = sl.beg;
		} else {
			Entry<Date, Activity<Float>> prevActEntry = this.activities.lowerEntry(sl.beg);
			if (prevActEntry==null) {
				return false;
			}
			prevAct = prevActEntry.getValue();
			prevActBeg = prevActEntry.getKey();
		}
		Entry<Date, Activity<Float>> nextActEntry = this.activities.higherEntry(sl.beg);
		Activity<Float> nextAct = nextActEntry.getValue();
		prevActEnd = new Date((long) (prevActBeg.getTime()+prevAct.getLength()));
		nextActBeg = nextActEntry.getKey();
		// If previous activity is "UNAVAILABLE" and ends at the same time the given slot begins 
		if (prevActEnd.equals(beg) && prevAct.getName().equals(unName)) {
			beg = prevActBeg;
		}
		// If next activity is "UNAVAILABLE" and begins at the same time the given slot ends
		if (nextActBeg.equals(end) && nextAct.getName().equals(unName)) {
			Date nextActEnd = new Date((long) (nextActBeg.getTime()+nextAct.getLength()));
			end = nextActEnd;
		}
		Activity<Float> act = new Activity<Float>((float) (end.getTime()-beg.getTime()), unPriority, unName);
		this.addActivity(beg, act);
		return true;
	}
	
	@Override
	public Iterator<PosAct<Date, Float>> activityIterator() {
		class ActivityIterator implements Iterator<PosAct<Date, Float>> {

			private Iterator<Entry<Date, Activity<Float>>> iterator;

			public ActivityIterator(NavigableMap<Date, Activity<Float>> activities) {
				this.iterator = activities.entrySet().iterator();
			}
			
			@Override
			public boolean hasNext() {
				return this.iterator.hasNext();
			}

			@Override
			public PosAct<Date, Float> next() {
				Entry<Date, Activity<Float>> entry = this.iterator.next();
				return new PosAct<Date, Float>(entry.getKey(), entry.getValue());
			}
			
			@Override
			public void remove() {
				this.iterator.remove();
			}
			
		}
		Iterator<PosAct<Date, Float>> it = new ActivityIterator(this.activities);
		return it;
	}

	@Override
	public Iterator<Slot<Float>> freeTimeIterator() {
		class FreeTimeIterator implements Iterator<Slot<Float>> {

			private Iterator<Entry<Date, Activity<Float>>> firstIt;
			private Iterator<Entry<Date, Activity<Float>>> secondIt;
			
			// TODO : problem with first and last slot : no first and last reference activity
			// Easy solution : add 0 length activities at the beginning and at the end !!
			public FreeTimeIterator(NavigableMap<Date, Activity<Float>> activities) {
				this.firstIt = activities.entrySet().iterator();
				this.secondIt = activities.entrySet().iterator();
				if (firstIt.hasNext())
					this.firstIt.next().getKey();
			}
			
			@Override
			public boolean hasNext() {
				return this.firstIt.hasNext();
			}

			@Override
			public Slot<Float> next() {
				Entry<Date, Activity<Float>> entry = secondIt.next();
				Activity<Float> act = entry.getValue();
				Date beg = new Date((long) (entry.getKey().getTime() + (Float) act.getLength()));
				Date end = firstIt.next().getKey();
				// If the slot size is null
				if (end.getTime()-beg.getTime() <= 0) {
					return this.hasNext() ? this.next() : null;
				}
				return new SlotImpl(beg, end);
			}
			
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		}
		Iterator<Slot<Float>> it = new FreeTimeIterator(this.activities);
		return it;
	}
	
	
	
	public class DateComparator implements Comparator<Date> {
		@Override
		public int compare(Date date1, Date date2) {
			Date d1 = (Date) date1;
			Date d2 = (Date) date2;
			if (d1.before(d2))
				return -1;
			if (d1.after(d2))
				return 1;
			return 0;
		}
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		Date t;
		Activity<Float> a;
		for(Entry<Date, Activity<Float>> entry: this.activities.entrySet()) {
			t = entry.getKey();
			a = entry.getValue();
			buffer.append("# "+t.toString()+" => "+a+"\n");
		}
		return buffer.toString();
	}

}
