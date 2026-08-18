package loadbalancer;

import loadbalancer.exception.NoInstancesAvailableException;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomStrategy implements SelectionStrategy{
    @Override
    public String select(List<String> instances) {
        List<String> snapshot = List.copyOf(instances);
        if (snapshot.isEmpty())
            throw new NoInstancesAvailableException("No instances available");
        return snapshot.get(ThreadLocalRandom.current().nextInt(snapshot.size()));
    }
}
