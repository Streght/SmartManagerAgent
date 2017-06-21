package com.smartmanageragent.smartagent;

import java.util.Date;
import java.util.Iterator;

import com.smartmanageragent.smartagent.timeTable.Activity;
import com.smartmanageragent.smartagent.timeTable.TimeTable;
import com.smartmanageragent.smartagent.timeTable.TimeTable.PosAct;
import com.smartmanageragent.smartagent.timeTable.TimeTableImpl;
import com.smartmanageragent.smartagent.timeTable.slot.Slot;
import com.smartmanageragent.smartagent.timeTable.slot.SlotImpl;

public class TestTT {

	@SuppressWarnings({ })
	public static void main(String[] args) {
		// Building TimeTable
		TimeTable<Date, Float> tt = new TimeTableImpl();
		buildTT(tt);
		// Testing TimeTable
		testTT(tt);
	}
	
	private static void buildTT(TimeTable<Date, Float> tt) {
		//================================
		Date d1 = new Date(0);
		tt.addActivity(d1, new Activity<Float>((float) 10000, TimeTableImpl.unPriority, TimeTableImpl.unName));
		//================================
		Date d2 = new Date(10000);
		tt.addActivity(d2, new Activity<Float>((float) 5000, TimeTableImpl.unPriority, "act1"));
		//================================
		Date d3 = new Date(20000);
		tt.addActivity(d3, new Activity<Float>((float) 10000, TimeTableImpl.unPriority, "act2"));
		//================================
		Date d4 = new Date(30000);
		tt.addActivity(d4, new Activity<Float>((float) 1000, TimeTableImpl.unPriority, "act3"));
		//================================
		Date d5 = new Date(40000);
		tt.addActivity(d5, new Activity<Float>((float) 20000, TimeTableImpl.unPriority, "act4"));
		//================================
		Date d6 = new Date(50000);
		tt.addActivity(d6, new Activity<Float>((float) 0, TimeTableImpl.unPriority, TimeTableImpl.unName));
		//================================
	}
	
	private static void testTT(TimeTable<Date, Float> tt) {
		// Activity iterator test
		Iterator<PosAct<Date, Float>> actIt = tt.activityIterator();
		while(actIt.hasNext()) {
			PosAct<Date, Float> act = actIt.next();
			System.out.println(act);
		}
		// Free time iterator test
		Iterator<Slot<Float>> ftIt = tt.freeTimeIterator();
		while(ftIt.hasNext()) {
			Slot<Float> slot = ftIt.next();
			System.out.println(slot);
		}
		// Adding free time slots
		SlotImpl sl1 = new SlotImpl(new Date(5000), new Date(7500));
		tt.addFreeTime(sl1);
		SlotImpl sl2 = new SlotImpl(new Date(7500), new Date(10000));
		tt.addFreeTime(sl2);
		SlotImpl sl3 = new SlotImpl(new Date(0), new Date(5000));
		tt.addFreeTime(sl3);
		// Forced to add manually first activity ...
		tt.addActivity(new Date(0), new Activity<Float>((float) 0, TimeTableImpl.unPriority, TimeTableImpl.unName));
		// =====> Displaying added free time slots
		ftIt = tt.freeTimeIterator();
		while(ftIt.hasNext()) {
			Slot<Float> slot = ftIt.next();
			System.out.println(slot);
		}
		// Removing free time slots
		tt.removeFreeTime(sl3);
		tt.removeFreeTime(sl1);
		tt.removeFreeTime(sl2);
		// =====> Displaying free time slots after removal
		ftIt = tt.freeTimeIterator();
		while(ftIt.hasNext()) {
			Slot<Float> slot = ftIt.next();
			System.out.println(slot);
		}
	}

}
