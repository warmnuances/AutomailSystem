/*W13 Team 1 (Tues 2.15pm)*/
package automail;

import charge.ChargeReceipt;
import charge.Charger;
import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import simulation.Building;
import simulation.Clock;
import simulation.IMailDelivery;

/**
 * The robot delivers mail!
 */
public class Robot {
	
    public static final int INDIVIDUAL_MAX_WEIGHT = 2000;

    IMailDelivery delivery;
    protected final String id;
    /** Possible states the robot can be in */
    public enum RobotState { DELIVERING, WAITING, RETURNING }
    public RobotState current_state;
    private int current_floor;
    private int destination_floor;
    private final MailPool mailPool;
    private final Charger charger;
    private boolean receivedDispatch;

    private MailItem deliveryItem = null;
    private MailItem tube = null;
    
    private int deliveryCounter;
    

    /**
     * Initiates the robot's location at the start to be at the mailroom
     * also set it to be waiting for mail.
     * @param delivery governs the final delivery
     * @param mailPool is the source of mail items
     */
    public Robot(IMailDelivery delivery, MailPool mailPool, Charger charger, int number){
    	this.id = "R" + number;
        // current_state = RobotState.WAITING;
    	this.current_state = RobotState.RETURNING;
        this.current_floor = Building.MAILROOM_LOCATION;
        this.delivery = delivery;
        this.mailPool = mailPool;
        this.charger = charger;
        this.receivedDispatch = false;
        this.deliveryCounter = 0;
    }
    
    /**
     * This is called when a robot is assigned the mail items and ready to dispatch for the delivery 
     */
    public void dispatch() {
    	receivedDispatch = true;
    }

    /**
     * This is called on every time step
     * @throws ExcessiveDeliveryException if robot delivers more than the capacity of the tube without refilling
     */
    public void operate() throws ExcessiveDeliveryException {   
    	switch(current_state) {
            /* This state is triggered when the robot is returning to the mailroom after a delivery */
    		case RETURNING:
                /* If its current position is at the mailroom, then the robot should change state */
                if(current_floor == Building.MAILROOM_LOCATION){
                	if (tube != null) {
                		this.mailPool.addToPool(this.tube);
                        System.out.printf("T: %3d > old addToPool [%s]%n", Clock.Time(), this.tube.toString());
                        this.tube = null;
                	}
                    /* Tell the sorter the robot is ready */
        			this.mailPool.registerWaiting(this);
                	changeState(RobotState.WAITING);
                } else {
                    /* If the robot is not at the mailroom floor yet, then move towards it! */
                    moveTowards(Building.MAILROOM_LOCATION);
                	break;
                }
    		case WAITING:
                /* If the StorageTube is ready and the Robot is waiting in the mailroom then start the delivery */
                if(!isEmpty() && this.receivedDispatch){
                	this.receivedDispatch = false;
                    this.deliveryCounter = 0; // reset delivery counter
                	setDestination();
                	changeState(RobotState.DELIVERING);
                }
                break;
    		case DELIVERING:
    			if(this.current_floor == this.destination_floor){ // If already here drop off either way
                    /* Delivery complete, report this to the simulator! */
                    /* Formula for calculating activity units is a round trip from mailroom to destination for now
                    *  with a single lookup*/
                    double activityUnits = calculateActivityUnits();
                    ChargeReceipt chargeReceipt = this.charger.chargeTenant(this.destination_floor, activityUnits);

                    this.delivery.deliver(new DeliveredMailItem(this.deliveryItem, chargeReceipt));
                    this.deliveryItem = null;
                    this.deliveryCounter++;
                    if(this.deliveryCounter > 2){  // Implies a simulation bug
                    	throw new ExcessiveDeliveryException();
                    }
                    /* Check if want to return, i.e. if there is no item in the tube*/
                    if(this.tube == null){
                    	changeState(RobotState.RETURNING);
                    }
                    else{
                        /* If there is another item, set the robot's route to the location to deliver the item */
                        this.deliveryItem = this.tube;
                        this.tube = null;
                        setDestination();
                        changeState(RobotState.DELIVERING);
                    }
    			} else {
                    /* The robot is not at the destination yet, move towards it! */
	                moveTowards(this.destination_floor);
    			}
                break;
    	}
    }

    /* Current formula is just a round trip, but is configurable if needed in the future*/
    private double calculateActivityUnits() {
        return 2 * (this.destination_floor - Building.MAILROOM_LOCATION) * ActivityUnit.ROBOT_MOVEMENT
               + ActivityUnit.REMOTE_LOOKUP;
    }

    /**
     * Sets the route for the robot
     */
    private void setDestination() {
        /* Set the destination floor */
        this.destination_floor = this.deliveryItem.getDestFloor();
    }

    /**
     * Generic function that moves the robot towards the destination
     * @param destination the floor towards which the robot is moving
     */
    private void moveTowards(int destination) {
        if(this.current_floor < destination){
            this.current_floor++;
        } else {
            this.current_floor--;
        }
    }
    
    private String getIdTube() {
    	return String.format("%s(%1d)", this.id, (this.tube == null ? 0 : 1));
    }
    
    /**
     * Prints out the change in state
     * @param nextState the state to which the robot is transitioning
     */
    private void changeState(RobotState nextState){
    	assert(!(this.deliveryItem == null && this.tube != null));
    	if (this.current_state != nextState) {
            System.out.printf("T: %3d > %7s changed from %s to %s%n", Clock.Time(), getIdTube(), this.current_state, nextState);
    	}
        this.current_state = nextState;
    	if(nextState == RobotState.DELIVERING){
            System.out.printf("T: %3d > %7s-> [%s]%n", Clock.Time(), getIdTube(), this.deliveryItem.toString());
    	}
    }

	public boolean isEmpty() {
		return (this.deliveryItem == null && this.tube == null);
	}

	public void addToHand(MailItem mailItem) throws ItemTooHeavyException {
		assert(this.deliveryItem == null);
        this.deliveryItem = mailItem;
		if (this.deliveryItem.weight > INDIVIDUAL_MAX_WEIGHT) throw new ItemTooHeavyException();
	}

	public void addToTube(MailItem mailItem) throws ItemTooHeavyException {
		assert(this.tube == null);
        this.tube = mailItem;
		if (this.tube.weight > INDIVIDUAL_MAX_WEIGHT) throw new ItemTooHeavyException();
	}

}
