package loadbalancer;

import loadbalancer.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class LoadBalancerTest {

    LoadBalancer loadBalancer;

    @BeforeEach
    void setup() {
        loadBalancer = new LoadBalancer(new RandomStrategy());
    }

    @Test
    void shouldRegisterInstance() {
       loadBalancer.registerInstance("123");
        assertTrue(loadBalancer.contains("123"));
    }
    @Test
    void shouldNotAllowDuplicateAddress(){
        loadBalancer.registerInstance("123");
        assertThrows(DuplicateInstanceException.class,
                () -> loadBalancer.registerInstance("123"));
    }
    @Test
    void shouldNotAddMoreThanCapacity() {
        for(int i=1;i<=10;i++){
            loadBalancer.registerInstance(String.valueOf(124 + i));
        }
        assertThrows(CapacityExceededException.class, () -> loadBalancer.registerInstance("135"));
    }
    @Test
    void shouldNotRegisterInstanceWithNullAddress() {
        assertThrows(InvalidAddressException.class,
                () -> loadBalancer.registerInstance(null));
    }
    @Test
    void shouldFailWhenTryingToFetchInstanceWhenNoneRegistered() {

        assertThrows(NoInstancesAvailableException.class, () -> loadBalancer.getInstance());
    }
    @Test
    void shouldOnlyReturnRegisteredInstancesAndVaryOverManyCalls() {
        loadBalancer.registerInstance("123");
        loadBalancer.registerInstance("124");
        loadBalancer.registerInstance("125");
        loadBalancer.registerInstance("126");
        loadBalancer.registerInstance("127");
        Set<String> resultsSeen = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            String result = loadBalancer.getInstance();
            assertTrue(loadBalancer.contains(result));
            resultsSeen.add(result);
        }
        assertTrue(resultsSeen.size() > 1);
    }
    @Test
    void shouldReturnInstancesInRoundRobinOrder() {
        LoadBalancer loadBalancer = new LoadBalancer(new RoundRobinStrategy());
        loadBalancer.registerInstance("ins1");
        loadBalancer.registerInstance("ins2");
        loadBalancer.registerInstance("ins3");

        assertEquals("ins1", loadBalancer.getInstance());
        assertEquals("ins2", loadBalancer.getInstance());
        assertEquals("ins3", loadBalancer.getInstance());
        assertEquals("ins1", loadBalancer.getInstance());
    }
    @Test
    void shouldKeepReturningSameInstanceWhenOnlyOneRegistered() {
        LoadBalancer loadBalancer = new LoadBalancer(new RoundRobinStrategy());
        loadBalancer.registerInstance("ins1");

        assertEquals("ins1", loadBalancer.getInstance());
        assertEquals("ins1", loadBalancer.getInstance());
        assertEquals("ins1", loadBalancer.getInstance());
    }

    @Test
    void shouldAllowOnlyOneRegistrationWhenManyThreadsRegisterSameAddressConcurrently() throws InterruptedException {

        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    loadBalancer.registerInstance("same-address");
                    successCount.incrementAndGet();
                } catch (DuplicateInstanceException e) {
                    // expected for 19 of the 20 threads — not a test failure
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        assertEquals(1, successCount.get());
        assertEquals(1, loadBalancer.countInstances()); // or however you expose size
    }

    @Test
    void shouldServeEachInstanceEquallyUnderConcurrentRoundRobinCalls() throws InterruptedException {
        LoadBalancer loadBalancer = new LoadBalancer(new RoundRobinStrategy());
        loadBalancer.registerInstance("ins1");
        loadBalancer.registerInstance("ins2");
        loadBalancer.registerInstance("ins3");

        int callsPerInstance = 100;
        int totalCalls = 3 * callsPerInstance; // 300 total calls, 3 instances

        ExecutorService executor = Executors.newFixedThreadPool(totalCalls);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(totalCalls);
        ConcurrentHashMap<String, AtomicInteger> resultCounts = new ConcurrentHashMap<>();
        resultCounts.put("ins1", new AtomicInteger(0));
        resultCounts.put("ins2", new AtomicInteger(0));
        resultCounts.put("ins3", new AtomicInteger(0));

        for (int i = 0; i < totalCalls; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    String result = loadBalancer.getInstance();
                    resultCounts.get(result).incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        assertEquals(callsPerInstance, resultCounts.get("ins1").get());
        assertEquals(callsPerInstance, resultCounts.get("ins2").get());
        assertEquals(callsPerInstance, resultCounts.get("ins3").get());

    }

    @Test
    void shouldNotContainInstanceAfterUnregistering() {
        loadBalancer.registerInstance("123");
        loadBalancer.unregisterInstance("123");

        assertFalse(loadBalancer.contains("123"));
    }
    @Test
    void shouldNotUnregisterNonRegisteredInstance() {
        assertThrows(InstanceNotRegisteredException.class,() -> loadBalancer.unregisterInstance("123"));
    }
    @Test
    void shouldReRegisterAfterUnregistering() {
        loadBalancer.registerInstance("123");
        loadBalancer.unregisterInstance("123");
        assertFalse(loadBalancer.contains("123"));
        loadBalancer.registerInstance("123");
        assertTrue(loadBalancer.contains("123"));
    }
    @Test
    void shouldNotThrowAndShouldReturnValidInstanceAfterUnregisteringMidRotation() {
        LoadBalancer loadBalancer = new LoadBalancer(new RoundRobinStrategy());
        loadBalancer.registerInstance("ins1");
        loadBalancer.registerInstance("ins2");
        loadBalancer.registerInstance("ins3");

        loadBalancer.getInstance(); // ins1, currentIndex now 1
        loadBalancer.getInstance(); // ins2, currentIndex now 2

        loadBalancer.unregisterInstance("ins2"); // list shrinks to [ins1, ins3]

        String result = loadBalancer.getInstance(); // this used to throw

        assertTrue(result.equals("ins1") || result.equals("ins3"));
        assertNotEquals("ins2", result);
    }

    @Test
    void shouldAllowOnlyOneUnregistrationWhenManyThreadsUnregisterSameAddressConcurrently() throws InterruptedException {
        loadBalancer = new LoadBalancer(new RandomStrategy());
        loadBalancer.registerInstance("same-address");

        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    loadBalancer.unregisterInstance("same-address");
                    successCount.incrementAndGet();
                } catch (InstanceNotRegisteredException e) {
                    // expected for 19 of the 20 threads — not a test failure
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        assertEquals(1, successCount.get());
        assertEquals(0, loadBalancer.countInstances());
    }
}
