package lb;

import lb.exception.DuplicateAddressLbException;
import lb.exception.InvalidAddressLbException;
import lb.exception.LbCapacityOverLoadLbException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LbTest {
    LbService lbService;

    @BeforeEach
    void setup() {
       this.lbService =  new LbService(new RandomInstanceFetchStrategy(),2);
    }

    @Test
    void shouldThrowForNullAddress() {
        assertThrows(InvalidAddressLbException.class, () -> lbService.register(null));
    }
    @Test
    void shouldRegisterValidAddress() {
        lbService.register("123");
        assertTrue(lbService.contains("123"));
    }
    @Test
    void shouldThrowForDuplicateAddress() {
        lbService.register("123");
        assertThrows(DuplicateAddressLbException.class, () -> lbService.register("123"));
    }
    @Test
    void shouldNotRegisterMoreThanCapacity() {
        lbService.register("123");
        lbService.register("124");
        assertThrows(LbCapacityOverLoadLbException.class, () -> lbService.register("125"));
    }
    @Test
    void shouldReturnInstanceRandomly() {
        lbService.register("123");
        lbService.register("124");
        String instance = lbService.getInstance();
        assertTrue(lbService.contains(instance));
    }

//    @Test
//    void shouldReturnInstancesViaRoundRobin() {
//        lbService = new LbService(new RoundRobinInstanceSelectionStrategy(),3);
//        lbService.register("123");
//        lbService.register("124");
//        lbService.register("125");
//        Map<String,Integer> hm = new HashMap<>();
//        for(int i=1;i<=9;i++){
//            hm.put(lbService.getInstance(),);
//        }
//
//
//    }

}
