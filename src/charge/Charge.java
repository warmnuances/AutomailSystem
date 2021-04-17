package charge;

import automail.MailItem;
import com.unimelb.swen30006.wifimodem.WifiModem;
import simulation.Building;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Charge {
    private Map<Integer, Double> _previousLookupServiceFee = new HashMap<>();
    private static final int ROBOT_MOVEMENT = 5;
    private static final double REMOTE_LOOKUP = 0.1;
    private final Double markupPercentage;
    private final Double activityUnitPrice;
    private static WifiModem wModem;

    public Charge(Double markupPercentage, Double activityUnitPrice) throws Exception {
        this.markupPercentage = markupPercentage;
        this.activityUnitPrice = activityUnitPrice;
        wModem = WifiModem.getInstance(Building.MAILROOM_LOCATION);
    }

    private void updatePreviousLookupServiceFee(int floorNumber, Double price){
        _previousLookupServiceFee.put(floorNumber, price);
    }

    private Double getPreviousLookupPrice(int floorNumber){
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

    public Double calculateCharge(MailItem mailItem) {
        int dest_floor = mailItem.getDestFloor();
        double service_fee = wModem.forwardCallToAPI_LookupPrice(dest_floor);
        if (service_fee > 0) {
            updatePreviousLookupServiceFee(dest_floor, service_fee);
        } else {
            service_fee = getPreviousLookupPrice(dest_floor);
        }
        int num_floors = dest_floor - Building.MAILROOM_LOCATION;
        // mailroom -> destination -> mailroom + lookup
        double activity_units = 2 * num_floors * ROBOT_MOVEMENT + REMOTE_LOOKUP;
        double activity_cost = activity_units * this.activityUnitPrice;
        double cost = service_fee + activity_cost;
        double charge = cost * (1+this.markupPercentage);
        return charge;
    }
}
