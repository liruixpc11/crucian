package crucian.benchmark.algorithms;

import crucian.algorithms.coarsening.BasicCoarsening;
import crucian.algorithms.coarsening.VirtualNetworkCoarsening;
import crucian.benchmark.AbstractMappingAlgorithm;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

/**
 * @author LiRuiNet
 *         14-3-2 下午9:24
 */
public abstract class BasicCoarsenedMappingAlgorithm extends AbstractMappingAlgorithm {
    private VirtualNetworkCoarsening coarsening = getCoarsening();
    private double coarseningRate = 1;

    protected VirtualNetworkCoarsening getCoarsening() {
        return new BasicCoarsening();
    }

    @Override
    public final void map(SubstrateNetwork substrateNetwork, VirtualNetwork virtualNetwork) throws Exception {
        VirtualNetwork coarsenedVirtualNetwork = coarsening.coarsen(virtualNetwork, substrateNetwork);
        doMap(substrateNetwork, coarsenedVirtualNetwork);
        coarseningRate = coarsenedVirtualNetwork.getVertexCount() / (double) virtualNetwork.getVertexCount();
        coarsening.undoCoarsen(coarsenedVirtualNetwork);
    }

    protected abstract void doMap(SubstrateNetwork substrateNetwork, VirtualNetwork virtualNetwork) throws Exception;
}
