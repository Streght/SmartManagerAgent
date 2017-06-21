package com.smartmanageragent.smartagent.timeTable;

import java.util.Iterator;

import com.smartmanageragent.smartagent.timeTable.slot.Slot;

public interface TimeTable<K, T> {

    /**
     * Adds an activity at a given position in the time table
     *
     * @param act
     * @param pos
     * @return previous activity associated to the position
     */
    Activity<T> addActivity(K pos, Activity<T> act);

    /**
     * Removes an activity at a given position in the time table
     *
     * @param pos
     * @return previous activity associated to the position
     */
    Activity<T> removeActivity(K pos);

    /**
     * Removes the first occurrence of an activity
     *
     * @param act
     * @return success
     */
    boolean removeActivity(Activity<T> act);

    /**
     * Returns the activity at the given position
     *
     * @param pos
     * @return activity
     */
    Activity<T> getActivity(K pos);

    /**
     * Adds a free time slot
     *
     * @param slot
     * @return success
     */
    boolean addFreeTime(Slot<T> slot);

    /**
     * Removes a free time slot
     *
     * @param slot
     * @return success
     */
    boolean removeFreeTime(Slot<T> slot);

    /**
     * Iterates over the activities
     *
     * @return iterator
     */
    Iterator<PosAct<K, T>> activityIterator();

    /**
     * Iterates over the free slots
     *
     * @return iterator
     */
    Iterator<Slot<T>> freeTimeIterator();

    /**
     * An activity and its current position
     *
     * @param <T>
     */
    class PosAct<K, T> {
        public K pos;
        public Activity<T> act;

        public PosAct(K pos, Activity<T> act) {
            this.pos = pos;
            this.act = act;
        }

        @Override
        public String toString() {
            return this.pos.toString() + " -> " + this.act.toString();
        }
    }


}
