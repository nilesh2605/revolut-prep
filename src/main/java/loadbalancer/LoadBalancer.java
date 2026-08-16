package loadbalancer;

import loadbalancer.exception.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LoadBalancer {
    private List<String> instances;
    private final int LOAD_BALANCER_CAPACITY = 10;
    private final SelectionStrategy selectionStrategy;

    public LoadBalancer(SelectionStrategy selectionStrategy) {
        this.selectionStrategy = selectionStrategy;
        this.instances = new CopyOnWriteArrayList<>();
    }

    public synchronized void registerInstance(String address){
        if(address == null || address.isEmpty())
            throw new InvalidAddressException("Cannot register instance without address");
        if(instances.size() >= LOAD_BALANCER_CAPACITY ){
            throw new CapacityExceededException("Cannot add more instances as capacity is full");
        }
        if(instances.contains(address)){
            throw new DuplicateInstanceException("Instance already registered");
        }
        instances.add(address);
    }
    public boolean contains(String address){
        return instances.contains(address);
    }

    public String getInstance() {
        if(instances.isEmpty())
            throw new NoInstancesAvailableException("No instances available");
        return selectionStrategy.select(instances);
    }
    public int countInstances() {
        return instances.size();
    }

    public synchronized void unregisterInstance(String address) {
        if(!instances.contains(address))
            throw new InstanceNotRegisteredException("Cannot unregister an instance that has not been registered");
        instances.remove(address);
    }
}
