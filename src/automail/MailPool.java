package automail;

import java.util.LinkedList;
import java.util.Comparator;
import java.util.ListIterator;

import charge.Charge;
import exceptions.ItemTooHeavyException;

/**
 * addToPool is called when there are mail items newly arrived at the building to add to the MailPool or
 * if a robot returns with some undelivered items - these are added back to the MailPool.
 * The data structure and algorithms used in the MailPool is your choice.
 * 
 */
public class MailPool {
	// Decorator
	
	public class ItemComparator implements Comparator<MailPoolItem> {
		@Override
		public int compare(MailPoolItem i1, MailPoolItem i2) {
			int order = 0;
			if (i1.destination < i2.destination) {
				order = 1;
			} else if (i1.destination > i2.destination) {
				order = -1;
			}
			return order;
		}
	}

	/*
	* Give priority to the item with estimated charge above the threshold
	* If both items are above the threshold, give to higher item with higher charge
	* If both items are below threshold, use destination floor?*/
	public class PriorityComparator implements Comparator<MailPoolItem> {
		@Override
		public int compare(MailPoolItem i1, MailPoolItem i2) {
			int order = 0;
			// if either of the items have a higher charge than the threshold
			if (Double.compare(i1.estimated_charge, charge_threshold) > 0 || Double.compare(i2.estimated_charge, charge_threshold) > 0) {
				// double.compare(i2,i1) returns -1 if i2<i1, 0 if i2==i1 and 1 if i2>i1, which is what we need since we need descending
				return Double.compare(i2.estimated_charge, i1.estimated_charge);
			} else {
				if (i1.destination < i2.destination) {
					order = 1;
				} else if (i1.destination > i2.destination) {
					order = -1;
				}
			}
			return order;
		}
	}
	
	private LinkedList<MailPoolItem> pool;
	private LinkedList<Robot> robots;
	private Charge charge;
	private double charge_threshold;

	public MailPool(double charge_threshold){
		// Start empty
		this.charge = charge;
		this.charge_threshold = charge_threshold;
		pool = new LinkedList<>();
		robots = new LinkedList<>();
	}

	/**
     * Adds an item to the mail pool
     * @param mailItem the mail item being added.
     */
	public void addToPool(MailItem mailItem) {
		MailPoolItem item = new MailPoolItem(mailItem);
		pool.add(item);
		pool.sort(new ItemComparator());
	}

	/**
     * load up any waiting robots with mailItems, if any.
     */
	public void loadItemsToRobot() throws ItemTooHeavyException {
		//List available robots
		ListIterator<Robot> i = robots.listIterator();
		while (i.hasNext()) loadItem(i);
	}
	
	//load items to the robot
	private void loadItem(ListIterator<Robot> i) throws ItemTooHeavyException {
		Robot robot = i.next();
		assert(robot.isEmpty());
		// System.out.printf("P: %3d%n", pool.size());
		ListIterator<MailPoolItem> j = pool.listIterator();
		if (pool.size() > 0) {
			try {
			robot.addToHand(j.next().mailItem); // hand first as we want higher priority delivered first
			j.remove();
			if (pool.size() > 0) {
				robot.addToTube(j.next().mailItem);
				j.remove();
			}
			robot.dispatch(); // send the robot off if it has any items to deliver
			i.remove();       // remove from mailPool queue
			} catch (Exception e) { 
	            throw e; 
	        } 
		}
	}

	/**
     * @param robot refers to a robot which has arrived back ready for more mailItems to deliver
     */	
	public void registerWaiting(Robot robot) { // assumes won't be there already
		robots.add(robot);
	}

}
