package lb;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomInstanceFetchStrategy implements InstanceSelectionStrategy{
    @Override
    public String getInstance(List<String> instances) {
       int index =  ThreadLocalRandom.current().nextInt(0, instances.size());
       return instances.get(index);
    }
}
