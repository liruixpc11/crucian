package crucian.benchmark.simulator;

import crucian.benchmark.MappingAlgorithm;
import vnreal.network.substrate.SubstrateNetwork;

/**
 * Created at 14-3-3 上午11:23.
 *
 * @author lirui
 */
public class Simulator implements Runnable {
    public Simulator(SimulatorListener simulatorListener, SubstrateNetwork substrateNetwork) {
        this.simulatorListener = simulatorListener;
        this.substrateNetwork = substrateNetwork;
    }

    public MappingAlgorithm getMappingAlgorithm() {
        return mappingAlgorithm;
    }

    public void setMappingAlgorithm(MappingAlgorithm mappingAlgorithm) {
        this.mappingAlgorithm = mappingAlgorithm;
    }

    public EventQueue getEventQueue() {
        return eventQueue;
    }

    @Override
    public void run() {
        mappingAlgorithm.initialize(substrateNetwork);

        while (simulatorListener.onEmpty(this))
            while (!eventQueue.isEmpty()) {
                Event event = eventQueue.dequeue();
                if (event.getTime() < currentTime) {
                    throw new AssertionError("事件时间小于当前时间");
                }

                if (event.getType().equals(Event.Type.Arrive)) {
                    try {
                        simulatorListener.preMap(this, event);
                        currentTime = event.getTime();
                        mappingAlgorithm.map(substrateNetwork, event.getVirtualNetwork());
                        simulatorListener.onSuccess(this, event);
                    } catch (Exception ex) {
                        simulatorListener.onError(this, event, ex);
                    }
                } else if (event.getType().equals(Event.Type.Depart)) {
                    event.getVirtualNetwork().freeAllDemands();
                    simulatorListener.onSuccess(this, event);
                } else {
                    throw new RuntimeException(String.format("未知事件类型：%s", event));
                }
            }
    }

    private EventQueue eventQueue = new EventQueue();
    private MappingAlgorithm mappingAlgorithm;
    private SimulatorListener simulatorListener;
    private SubstrateNetwork substrateNetwork;
    private int currentTime = 0;
}
