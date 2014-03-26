package crucian.benchmark.simulator;

import crucian.benchmark.MappingAlgorithm;
import vnreal.network.substrate.SubstrateNetwork;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created at 14-3-3 上午11:23.
 *
 * @author lirui
 */
public class Simulator implements Runnable {
    public Simulator(String name, SimulatorListener simulatorListener, SubstrateNetwork substrateNetwork) {
        this(simulatorListener, substrateNetwork);
        this.name = name;
    }

    public Simulator(SimulatorListener simulatorListener, SubstrateNetwork substrateNetwork) {
        this.id = System.nanoTime();
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

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public SubstrateNetwork getSubstrateNetwork() {
        return substrateNetwork;
    }

    public void addPlugins(SimulatorPlugin ...simulatorPlugins) {
        Collections.addAll(plugins, simulatorPlugins);
    }

    @Override
    public void run() {
        for (SimulatorPlugin plugin : plugins) {
            plugin.initialize(this);
        }

        mappingAlgorithm.initialize(substrateNetwork);

        while (simulatorListener.onEmpty(this)) {
            while (!eventQueue.isEmpty()) {
                Event event = eventQueue.dequeue();
                if (event.getTime() < currentTime) {
                    throw new AssertionError("事件时间小于当前时间");
                }

                for (SimulatorPlugin plugin : plugins) {
                    plugin.onEventBegin(this, event);
                }

                long startTime = System.nanoTime();
                if (event.getType().equals(Event.Type.Arrive)) {
                    try {
                        simulatorListener.preMap(this, event);
                        currentTime = event.getTime();
                        startTime = System.nanoTime();
                        mappingAlgorithm.map(substrateNetwork, event.getVirtualNetwork());
                        event.setHandleNanoseconds(System.nanoTime() - startTime);
                        event.setSuccess(true);
                        simulatorListener.onSuccess(this, event);
                    } catch (Exception ex) {
                        event.setHandleNanoseconds(System.nanoTime() - startTime);
                        event.setError(ex);
                        simulatorListener.onError(this, event, ex);
                    }
                } else if (event.getType().equals(Event.Type.Depart)) {
                    startTime = System.nanoTime();
                    event.getVirtualNetwork().freeAllDemands();
                    event.setHandleNanoseconds(System.nanoTime() - startTime);
                    event.setSuccess(true);
                    simulatorListener.onSuccess(this, event);
                } else {
                    throw new RuntimeException(String.format("未知事件类型：%s", event));
                }

                for (SimulatorPlugin plugin : plugins) {
                    plugin.onEventEnd(this, event);
                }
            }
        }

        for (SimulatorPlugin plugin : plugins) {
            plugin.uninitialize(this);
        }
    }

    private long id;
    private String name;
    private EventQueue eventQueue = new EventQueue();
    private MappingAlgorithm mappingAlgorithm;
    private SimulatorListener simulatorListener;
    private SubstrateNetwork substrateNetwork;
    private int currentTime = 0;
    private List<SimulatorPlugin> plugins = new ArrayList<>();
}
