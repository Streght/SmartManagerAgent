package com.smartmanageragent.smartagent.agent;

public class Results {
	
	public int expected;
	public int acceptances;
	public int refusals;
	
	public Results(int exp) {
		this.expected = exp;
		this.acceptances = 0;
		this.refusals = 0;
	}
	
	/** Returns true if vote is finished, else false
	 * @return finished
	 */
	public boolean finished() {
		return this.expected == this.acceptances+this.refusals;
	}

	/** Returns true if vote is finished and proposition has been accepted
	 * @return accepted
	 */
	public boolean accepted() {
		return this.finished() && this.acceptances==this.expected;
	}

	/** Returns true if vote is finished and proposition has been refused
	 * @return refused
	 */
	public boolean refused() {
		return this.finished() && this.refusals>0;
	}
	
	@Override
	public String toString() {
		return "Acc:"+this.acceptances+"\\"+this.expected+";Ref:"+this.refusals+"\\"+this.expected;
	}
	
}