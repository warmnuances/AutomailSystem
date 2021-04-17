package charge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Charge {
    private static Map<Integer, Double> _previousLookupPrice = new HashMap<>();

    private static void addPreviousPrice(int floorNumber, Double price){
        _previousLookupPrice.put(floorNumber, price);
    }

    private static Double getPreviousLookupPrice(int floorNumber){
        return _previousLookupPrice.get(floorNumber);
    }




}
