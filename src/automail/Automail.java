/*W13 Team 1 (Tues 2.15pm)*/
package automail;

import charge.Charger;
import simulation.IMailDelivery;

public class Automail {
	      
    public Robot[] robots;
    public MailPool mailPool;

    public Automail(MailPool mailPool, IMailDelivery delivery, Charger charger, int numRobots) {
        /* Initialize the MailPool */
    	
    	this.mailPool = mailPool;

        /* Initialize robots */
    	robots = new Robot[numRobots];
    	for (int i = 0; i < numRobots; i++) robots[i] = new Robot(delivery, mailPool, charger, i);
    }
    
}
