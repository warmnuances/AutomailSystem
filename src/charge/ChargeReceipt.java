/*W13 Team 1 (Tues 2.15pm)*/
package charge;

import automail.Receipt;

public class ChargeReceipt implements Receipt {
    private double activityUnits;
    private double serviceFee;
    private double activityCost;
    private double cost;
    private double charge;

    public ChargeReceipt(double activityUnits, double serviceFee, double activityCost, double cost, double charge) {
        this.activityUnits = activityUnits;
        this.serviceFee = serviceFee;
        this.activityCost = activityCost;
        this.cost = cost;
        this.charge = charge;
    }

    public double getActivityUnits() {
        return activityUnits;
    }

    public double getServiceFee() {
        return serviceFee;
    }

    public double getActivityCost() {
        return activityCost;
    }

    public double getCost() {
        return cost;
    }

    public double getCharge() {
        return charge;
    }

    public String printReceipt() {
        return String.format("Charge: %.2f | Cost: %.2f | Fee: %.2f | Activity: %.2f",
                this.getCharge(), this.getCost(), this.getServiceFee(), this.getActivityUnits());
    }
}
