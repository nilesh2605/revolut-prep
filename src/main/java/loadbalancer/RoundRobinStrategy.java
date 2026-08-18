package loadbalancer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinStrategy implements SelectionStrategy {

    private final AtomicInteger currentIndex = new AtomicInteger(0);
    @Override
    public String select(List<String> instances) {
        int index = currentIndex.getAndUpdate(i -> (i + 1) % instances.size()) % instances.size();
        return instances.get(index);
    }
}
