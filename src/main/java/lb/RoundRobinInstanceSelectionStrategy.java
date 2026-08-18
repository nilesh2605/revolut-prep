package lb;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinInstanceSelectionStrategy implements InstanceSelectionStrategy{
    @Override
    public String getInstance(List<String> instances) {
        AtomicInteger currentIndex = new AtomicInteger(0);

//        currentIndex = (currentIndex.getAndAdd(i -> (i +1)%instances.size());
//        return instances.get(currentIndex);
        return null;

    }
}
