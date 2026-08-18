package parkingfeecalculator;

public class ParkingFeeCalculator {

    private static final int FREE_MINUTES = 60;
    private static final int RATE_PER_HOUR = 20;

    public int calculateFee(int minutes) {
        if (minutes < 0) {
            throw new IllegalArgumentException("Parking duration cannot be negative");
        }
        if (minutes <= FREE_MINUTES) {
            return 0;
        }
        return paidHours(minutes) * RATE_PER_HOUR;
    }

    private int paidHours(int minutes) {
        return (int) Math.ceil((double) minutes / FREE_MINUTES) - 1;
    }
}
