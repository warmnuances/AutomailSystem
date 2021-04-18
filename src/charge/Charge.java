package charge;

import automail.MailItem;
import com.unimelb.swen30006.wifimodem.WifiModem;
import simulation.Building;

import java.util.HashMap;
import java.util.Map;

public class Charge {
    private static Map<Integer, Double> _previousLookupServiceFee = new HashMap<>();
    private static final int ROBOT_MOVEMENT = 5;
    private static final double REMOTE_LOOKUP = 0.1;
    private static Double markupPercentage;
    private static Double activityUnitPrice;
    private static WifiModem wModem;

    private static int totalLookups = 0;
    private static int failedLookups = 0;
    private static int successfulLookups = 0;

    public static void setMarkupPercentage(double markupPct) {
        markupPercentage = markupPct;
    }

    public static void setActivityUnitPrice(double actUnitPrice) {
        activityUnitPrice = actUnitPrice;
    }

    public static void setWModem(int installedOnFloor) {
        try{
            wModem = WifiModem.getInstance(installedOnFloor);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private static void updatePreviousLookupServiceFee(int floorNumber, Double price){
        _previousLookupServiceFee.put(floorNumber, price);
    }

    public static Double getPreviousLookupServiceFee(int floorNumber){
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

    public static Double getServiceFee(int floorNumber) {
        double service_fee = wModem.forwardCallToAPI_LookupPrice(floorNumber);
        totalLookups++;
        if (service_fee > 0) {
            updatePreviousLookupServiceFee(floorNumber, service_fee);
            successfulLookups++;
        } else {
            service_fee = getPreviousLookupServiceFee(floorNumber);
            failedLookups++;
        }
        return service_fee;
    }

    public static Double calculateActivityUnits(int floorNumber) {
        // mailroom -> destination -> mailroom + lookup
        int num_floors = floorNumber - Building.MAILROOM_LOCATION;
        return 2 * num_floors * ROBOT_MOVEMENT + REMOTE_LOOKUP;
    }

    public static Double calculateActivityCost(Double activityUnits) {
        return activityUnits * activityUnitPrice;
    }

    public static Double calculateTotalCost(Double activityCost, Double serviceFee) {
        return activityCost + serviceFee;
    }

    public static Double calculateTotalCharge(Double totalCost) {
        return totalCost * (1 + markupPercentage);
    }

    public static Double calculateCharge(MailItem mailItem) {
        int dest_floor = mailItem.getDestFloor();
        double service_fee = getServiceFee(dest_floor);
        // mailroom -> destination -> mailroom + lookup
        double activity_units = calculateActivityUnits(dest_floor);
        double activity_cost = calculateActivityCost(activity_units);
        double cost = calculateTotalCost(activity_cost, service_fee);
        double charge = calculateTotalCharge(cost);

        return charge;
    }

    public static int getTotalLookups() {
        return totalLookups;
    }

    public static int getFailedLookups() {
        return failedLookups;
    }

    public static int getSuccessfulLookups() {
        return successfulLookups;
    }
}
