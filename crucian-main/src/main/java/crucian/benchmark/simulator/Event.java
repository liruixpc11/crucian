package crucian.benchmark.simulator;

import vnreal.network.virtual.VirtualNetwork;

/**
 * Created at 14-3-3 上午11:14.
 *
 * @author lirui
 */
public class Event {
    public static enum Type {
        Arrive,
        Depart
    }

    public Event(Type type, int time, VirtualNetwork virtualNetwork) {
        this.type = type;
        this.time = time;
        this.virtualNetwork = virtualNetwork;
    }

    public Type getType() {
        return type;
    }

    public int getTime() {
        return time;
    }

    public VirtualNetwork getVirtualNetwork() {
        return virtualNetwork;
    }

    @Override
    public String toString() {
        return "Event{" +
                "type=" + type +
                ", time=" + time +
                ", virtualNetwork=" + virtualNetwork +
                '}';
    }

    private Type type;
    private int time;
    private VirtualNetwork virtualNetwork;
}
