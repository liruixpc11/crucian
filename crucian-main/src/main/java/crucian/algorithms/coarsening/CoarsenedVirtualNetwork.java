package crucian.algorithms.coarsening;

import vnreal.network.virtual.VirtualNetwork;

public class CoarsenedVirtualNetwork extends VirtualNetwork {
    private int coarsenLevel = 1;

    public CoarsenedVirtualNetwork(int layer, boolean autoUnregisterConstraints) {
        super(layer, autoUnregisterConstraints);
    }

    public CoarsenedVirtualNetwork(int layer) {
        super(layer);
    }

    public CoarsenedVirtualNetwork(int layer, int coarsenLevel) {
        super(layer);
        this.coarsenLevel = coarsenLevel;
    }

    public VirtualNetwork getBaseNetwork() {
        return baseNetwork;
    }

    public void setBaseNetwork(VirtualNetwork baseNetwork) {
        this.baseNetwork = baseNetwork;
    }

    private VirtualNetwork baseNetwork;

    public int getCoarsenLevel() {
        return coarsenLevel;
    }

    public void setCoarsenLevel(int coarsenLevel) {
        this.coarsenLevel = coarsenLevel;
    }
}
