package automail;

import charge.ChargeReceipt;

public class DeliveredMailItem {
    private final ChargeReceipt chargeReceipt;
    private final MailItem mailItem;
    public DeliveredMailItem(MailItem mailItem, ChargeReceipt chargeReceipt) {
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
        return String.format("%s | %s", this.mailItem.toString(), this.chargeReceipt.printReceipt());
    }
}
