package crucian.algorithms.coarsening;

import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

public interface VirtualNetworkCoarsening {
    public VirtualNetwork coarsen(VirtualNetwork toCoarsen, SubstrateNetwork substrateNetwork);

    public VirtualNetwork undoCoarsen(VirtualNetwork coarsened);
}
