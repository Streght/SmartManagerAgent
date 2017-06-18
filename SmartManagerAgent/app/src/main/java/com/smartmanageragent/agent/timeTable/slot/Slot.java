package timeTable.slot;

/** A slot of time
 * @param <T>
 */
public interface Slot<T> extends Comparable<Slot<T>> {
	
	// The method compareTo is used to determine slots relative positions (before/after/during)
	
	/** Returns the reference position of the slot
	 * @return the information allowing the location of the slot in the time table
	 */
	public Object getRef();
	
	/** Union intersection of this slot with another
	 * @param s
	 * @return a slot corresponding to the intersection of two slots,
	 * null if empty intersection
	 */
	public Slot<T> intersection(Slot<T> s);
	
	/** Returns true if the length T fits in the slot
	 * @param length
	 * @return true if the slot fits in the other
	 */
	public boolean fits(T length);
	
}
