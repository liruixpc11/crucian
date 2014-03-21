package crucian.algorithms.coarsening;

import mulavito.algorithms.AbstractAlgorithmStatus;
import vnreal.algorithms.SingleNetworkMappingAlgorithm;
import vnreal.network.NetworkStack;
import vnreal.network.virtual.VirtualNetwork;

import java.util.List;

public class BasicCoarsenAlgorithm extends AbstractCoarsenAlgorithm {
    private SingleNetworkMappingAlgorithm mapAlgorithm;
    private VirtualNetworkCoarsening coarsening;

    protected BasicCoarsenAlgorithm(NetworkStack networkStack,
                                    SingleNetworkMappingAlgorithm mapAlgorithm,
                                    VirtualNetworkCoarsening coarsening) {
        super(networkStack);
        this.mapAlgorithm = mapAlgorithm;
        this.coarsening = coarsening;
    }

    @Override
    protected boolean process(VirtualNetwork graph) {
        try {
            mapAlgorithm.mapNetwork(getSubstrateNetwork(), graph);
            return true;
        } catch (Exception ex) {
            System.err.println(ex.toString());
            return false;
        }
    }

    @Override
    protected VirtualNetwork coarsen(VirtualNetwork toCoarsen) {
        return coarsening.coarsen(toCoarsen, getSubstrateNetwork());
    }

    @Override
    protected VirtualNetwork undoCoarsen(VirtualNetwork coarsened) {
        return coarsening.undoCoarsen(coarsened);
    }

    @Override
    protected boolean preRun() {
        return true;
    }

    @Override
    protected void postRun() {

    }

    @Override
    public List<AbstractAlgorithmStatus> getStati() {
        return null;
    }
}
