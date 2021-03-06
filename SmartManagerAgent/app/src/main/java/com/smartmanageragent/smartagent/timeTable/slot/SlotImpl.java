package com.smartmanageragent.smartagent.timeTable.slot;

import java.io.Serializable;
import java.util.Date;

public class SlotImpl implements Slot<Float> {

	private static final long serialVersionUID = 1L;
	
	public Date beg;
	public Date end;
	
	public SlotImpl(Date beg, Date end) {
		this.beg = beg;
		this.end = end;
	}
	
	@Override
	public Serializable getRef() {
		return this.beg;
	}
	
	@Override
	public Slot<Float> intersection(Slot<Float> s) {
		SlotImpl slot = (SlotImpl) s;
		SlotImpl res = null;
		// If this begins before slot, and ends after slot beginning
		if (this.beg.compareTo(slot.beg) <=0 && this.end.compareTo(slot.beg) >= 0) {
			Date b = slot.beg;
			Date e;
			// If this ends before slot ends
			if (this.end.compareTo(slot.end) <=0) {
				e = this.end;
			// If this ends after slot ends
			} else {
				e = slot.end;
			}
			res = new SlotImpl(b, e);
		// If this begins after slot, and before slot ends
		} else if (this.beg.compareTo(slot.beg) >=0 && this.beg.compareTo(slot.end) <= 0) {
			Date b = this.beg;
			Date e;
			// If slot ends before this ends
			if (this.end.compareTo(slot.end) >= 0) {
				e = slot.end;
			// If slot ends after this ends
			} else {
				e = this.end;
			}
			res = new SlotImpl(b, e);
		}
		return res;
	}

	@Override
	public boolean fits(Float length) {
		// TODO : improve
		long diff = this.end.getTime() - this.beg.getTime();
		return length<=diff;
	}
	
	@Override
	public int compareTo(Slot<Float> s) {
		SlotImpl slot = (SlotImpl) s;
		if (this.end.before(slot.beg))
			return -1;
		if (this.beg.after(slot.end))
			return 1;
		return 0;
	}
	
	@Override
	public String toString() {
		return "FROM: "+this.beg.toString()+" ; TO:"+this.end.toString()+" => "
				+(this.end.getTime()-this.beg.getTime());
	}

}
