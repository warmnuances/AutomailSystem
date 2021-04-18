package automail;
import charge.Charge;

public class MailPoolItem {
    int destination;
    double estimated_charge;
    double service_fee;
    double activityUnits;
    double cost;
    MailItem mailItem;
    // Use stable sort to keep arrival time relative positions
    // Sorting Objects uses mergesort which is stable.
    public MailPoolItem(MailItem mailItem) {
        this.destination = mailItem.getDestFloor();
        this.estimated_charge = Charge.calculateCharge(mailItem);
        this.service_fee = Charge.getPreviousLookupServiceFee(destination);
        this.activityUnits = Charge.calculateActivityUnits(destination);
        this.cost = Charge.calculateTotalCost(Charge.calculateActivityCost(this.activityUnits), this.service_fee);
        this.mailItem = mailItem;
    }

    @Override
    public String toString() {
        return String.format("%s | Charge: %.2f | Cost: %.2f | Fee: %.2f | Activity: %.2f",
                this.mailItem.toString(), this.estimated_charge, this.cost, this.service_fee, this.activityUnits);
    }
}
