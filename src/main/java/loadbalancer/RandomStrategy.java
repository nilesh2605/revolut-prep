package loadbalancer;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomStrategy implements SelectionStrategy{
    @Override
    public String select(List<String> instances) {
            return instances.get(ThreadLocalRandom.current().nextInt(instances.size()));
    }
}
