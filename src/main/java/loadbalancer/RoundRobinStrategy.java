package loadbalancer;

import loadbalancer.exception.NoInstancesAvailableException;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinStrategy implements SelectionStrategy {

    private final AtomicInteger currentIndex = new AtomicInteger(0);
    @Override
    public String select(List<String> instances) {
        List<String> snapshot = List.copyOf(instances); // one read, one array, frozen
        if (snapshot.isEmpty())
            throw new NoInstancesAvailableException("No instances available");
        int index = currentIndex.getAndUpdate(i -> (i + 1) % snapshot.size()) % snapshot.size();
        return snapshot.get(index);
    }
}
