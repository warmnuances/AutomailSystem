package charge;

import automail.ActivityUnit;
import automail.MailItem;
import com.unimelb.swen30006.wifimodem.WifiModem;
import simulation.Building;

import java.util.HashMap;
import java.util.Map;

public class Charger {
    // a hashmap that contains service fee previously lookup'd keyed by servicing floor
    private Map<Integer, Double> _previousLookupServiceFee = new HashMap<>();
    private final double markupPercentage;
    private final double activityUnitPrice;
    private WifiModem wModem;

    private int totalLookups = 0;
    private int failedLookups = 0;
    private int successfulLookups = 0;

    public Charger(double markupPercentage, double activityUnitPrice, int installedOnFloor) {
        this.markupPercentage = markupPercentage;
        this.activityUnitPrice = activityUnitPrice;
        try{
            this.wModem = WifiModem.getInstance(installedOnFloor);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void updatePreviousLookupServiceFee(int floorNumber, Double price){
        _previousLookupServiceFee.put(floorNumber, price);
    }

    private double getPreviousLookupServiceFee(int floorNumber){
        if (_previousLookupServiceFee.get(floorNumber) != null) {
            return _previousLookupServiceFee.get(floorNumber);
        } else {
            double averageServiceFee = 0.0;
            for(Map.Entry<Integer, Double> entry: _previousLookupServiceFee.entrySet()) {
                averageServiceFee += entry.getValue();
            }
            return averageServiceFee / _previousLookupServiceFee.size();
        }
    }

    private double getServiceFee(int floorNumber) {
        double service_fee = wModem.forwardCallToAPI_LookupPrice(floorNumber);
        this.totalLookups++;
        if (service_fee > 0) {
            updatePreviousLookupServiceFee(floorNumber, service_fee);
            this.successfulLookups++;
        } else {
            service_fee = getPreviousLookupServiceFee(floorNumber);
            this.failedLookups++;
        }
        return service_fee;
    }

    private double estimateActivityUnits(int floorNumber) {
        // mailroom -> destination -> mailroom + lookup
        int num_floors = floorNumber - Building.MAILROOM_LOCATION;
        return 2 * num_floors * ActivityUnit.ROBOT_MOVEMENT + ActivityUnit.REMOTE_LOOKUP;
    }

    private double calculateActivityCost(Double activityUnits) {
        return activityUnits * activityUnitPrice;
    }

    private double calculateTotalCost(Double activityCost, Double serviceFee) {
        return activityCost + serviceFee;
    }

    private double calculateTotalCharge(Double totalCost) {
        return totalCost * (1 + markupPercentage);
    }

    public ChargeReceipt chargeTenant(int destinationFloor, double activityUnits) {
        double serviceFee = getServiceFee(destinationFloor);
        double activityCost = calculateActivityCost(activityUnits);
        double cost = calculateTotalCost(activityCost, serviceFee);
        double charge = calculateTotalCharge(cost);

        return new ChargeReceipt(activityUnits, serviceFee, activityCost, cost, charge);
    }

    public double estimateCharge(MailItem mailItem) {
        int dest_floor = mailItem.getDestFloor();
        double serviceFee = getServiceFee(dest_floor);
        double activityUnits = estimateActivityUnits(dest_floor);
        double activityCost = calculateActivityCost(activityUnits);
        double cost = calculateTotalCost(activityCost, serviceFee);
        double charge = calculateTotalCharge(cost);

        return charge;
    }

    public int getTotalLookups() {
        return this.totalLookups;
    }

    public int getFailedLookups() {
        return this.failedLookups;
    }

    public int getSuccessfulLookups() {
        return this.successfulLookups;
    }
}