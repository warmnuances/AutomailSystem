package charge;

public class ChargeReceipt {
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
}
