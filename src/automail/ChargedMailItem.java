package automail;

import charge.ChargeReceipt;

public class ChargedMailItem{
    private final ChargeReceipt chargeReceipt;
    private final MailItem mailItem;
    public ChargedMailItem(MailItem mailItem, ChargeReceipt chargeReceipt) {
        this.mailItem = mailItem;
        this.chargeReceipt = chargeReceipt;
    }

    public ChargeReceipt getChargeReceipt() {
        return this.chargeReceipt;
    }

    public MailItem getMailItem() {
        return this.mailItem;
    }

    @Override
    public String toString() {
        return String.format("%s | Charge: %.2f | Cost: %.2f | Fee: %.2f | Activity: %.2f",
                this.mailItem.toString(), this.chargeReceipt.getCharge(), this.chargeReceipt.getCost(),
                this.chargeReceipt.getServiceFee(), this.chargeReceipt.getActivityUnits());
    }
}
