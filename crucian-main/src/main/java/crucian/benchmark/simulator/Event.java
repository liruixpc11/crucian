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

    public void setError(Exception cause) {
        this.success = false;
        this.cause = cause;
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

    public long getHandleNanoseconds() {
        return handleNanoseconds;
    }

    public void setHandleNanoseconds(long handleNanoseconds) {
        this.handleNanoseconds = handleNanoseconds;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Exception getCause() {
        return cause;
    }

    public void setCause(Exception cause) {
        this.cause = cause;
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
    private long handleNanoseconds;
    private boolean success;
    private Exception cause;
}
