package lb;

import lb.exception.DuplicateAddressLbException;
import lb.exception.InvalidAddressLbException;
import lb.exception.LbCapacityOverLoadLbException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LbService {

    private final List<String> instances;

    private  int capacity;

    private final InstanceSelectionStrategy getInstanceStrategy;
    public LbService(InstanceSelectionStrategy getInstanceStrategy, int capacity){
        this.instances = new CopyOnWriteArrayList<>();
        this.getInstanceStrategy = getInstanceStrategy;
        this.capacity = capacity;
    }

    public synchronized void register(String address) {
        if(address == null || address.isEmpty() ) {
            throw new InvalidAddressLbException("Cannot register instance as Invalid address found " + address);
        }
        if(instances.size() >= capacity) {
            throw new LbCapacityOverLoadLbException("Cannot register more instances");
        }
        if(instances.contains(address)){
            throw new DuplicateAddressLbException("Instance already registered with address " + address);
        }
        instances.add(address);
    }

    public boolean contains(String address) {
        return instances.contains(address);
    }
    public String getInstance() {
        return getInstanceStrategy.getInstance(instances);
    }
}
