package parkingfeecalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParkingLotTest {

    ParkingFeeCalculator feeCalculator = new ParkingFeeCalculator();
    ParkingLot parkingLot;

    @BeforeEach
    void setup() {
        parkingLot = new ParkingLot(1, feeCalculator);
    }

    @Test
    void shouldMarkVehicleAsOccupiedAfterCheckIn() {
        parkingLot.checkIn("KA01AB1234");
        assertTrue(parkingLot.isOccupied("KA01AB1234"));
    }

    @Test
    void shouldNotCheckInAlreadyCheckedInVehicle() {
        parkingLot.checkIn("MH12ML1320");

        assertThrows(IllegalArgumentException.class,
                () -> parkingLot.checkIn("MH12ML1320"));
    }

    @Test
    void shouldNotCheckInWhenLotIsFull() {
        parkingLot.checkIn("MH12ML1321");

        assertThrows(IllegalArgumentException.class,
                () -> parkingLot.checkIn("MH12ML1322"));
    }

    @Test
    void shouldNotCheckoutForNonCheckedInVehicle() {
        assertThrows(IllegalArgumentException.class,
                () -> parkingLot.checkOut("MH12ML1323", 1));
    }
}