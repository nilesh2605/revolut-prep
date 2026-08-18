package parkingfeecalculator;

import java.util.HashSet;
import java.util.Set;

public class ParkingLot {
    private final int capacity;
    private final Set<String> parkedVehicles;
    private final ParkingFeeCalculator parkingFeeCalculator;

    public ParkingLot(int capacity, ParkingFeeCalculator feeCalculator) {
        this.capacity = capacity;
        this.parkingFeeCalculator = feeCalculator;
        this.parkedVehicles = new HashSet<>();
    }

    public void checkIn(String vehicleId) {
        if (isOccupied(vehicleId)) {
            throw new IllegalArgumentException("Vehicle already checked-in");
        }
        if (parkedVehicles.size() >= capacity) {
            throw new IllegalArgumentException("Parking lot is full");
        }
        parkedVehicles.add(vehicleId);
    }

    public int checkOut(String vehicleId, int minutesParked) {
        if(!isOccupied(vehicleId)){
            throw new IllegalArgumentException("Vehicle not checked in");
        }
        return 0;
    }
    public boolean isOccupied(String vehicleId) {
        return parkedVehicles.contains(vehicleId);
    }
}
