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
    private VirtualNetworkCoarsening coarsening;
    private double coarseningRate = 1;
    private long startTime;
    private long coarsenTime;
    private long mapTime;
    private long uncoarsenTime;

    protected VirtualNetworkCoarsening createCoarsening() {
        return new BasicCoarsening();
    }

    private VirtualNetworkCoarsening getCoarsening() {
        if (coarsening == null) {
            return coarsening = createCoarsening();
        }

        return coarsening;
    }

    @Override
    public final void map(SubstrateNetwork substrateNetwork, VirtualNetwork virtualNetwork) throws Exception {
        startTime = System.nanoTime();
        VirtualNetwork coarsenedVirtualNetwork = getCoarsening().coarsen(virtualNetwork, substrateNetwork);
        coarsenTime = System.nanoTime();
        coarseningRate = (double) coarsenedVirtualNetwork.getVertexCount() / virtualNetwork.getVertexCount();
        doMap(substrateNetwork, coarsenedVirtualNetwork);
        mapTime = System.nanoTime();
        coarsening.undoCoarsen(coarsenedVirtualNetwork);
        uncoarsenTime = System.nanoTime();
    }

    public double getCoarseningRate() {
        return coarseningRate;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getCoarsenTime() {
        return coarsenTime;
    }

    public long getMapTime() {
        return mapTime;
    }

    public long getUncoarsenTime() {
        return uncoarsenTime;
    }

    protected abstract void doMap(SubstrateNetwork substrateNetwork, VirtualNetwork virtualNetwork) throws Exception;
}
