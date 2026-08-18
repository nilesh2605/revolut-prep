package parkingfeecalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParkingFeeCalculatorTest {

    ParkingFeeCalculator parkingFeeCalculator;

    @BeforeEach
    void setup() {
        parkingFeeCalculator = new ParkingFeeCalculator();
    }

    @Test
    void shouldReturnZeroFeeForVeryShortStay() {
        int fee = parkingFeeCalculator.calculateFee(10);
        assertEquals(0, fee);
    }

    @Test
    void shouldChargeForOneHourWhenStayIs90Minutes() {
        int fee = parkingFeeCalculator.calculateFee(90);
        assertEquals(20, fee);
    }

    @Test
    void shouldChargeHourlyForMoreThanOneHour() {
        int fee = parkingFeeCalculator.calculateFee(180);
        assertEquals(40, fee);
    }

    @Test
    void shouldThrowWhenMinutesIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> parkingFeeCalculator.calculateFee(-10));
    }

    @Test
    void shouldReturnZeroFeeForExactlyOneHour() {
        int fee = parkingFeeCalculator.calculateFee(60);
        assertEquals(0, fee);
    }

    @Test
    void shouldReturnOneHourFeeForExactly61Minutes() {
        int fee = parkingFeeCalculator.calculateFee(61);
        assertEquals(20, fee);
    }
}