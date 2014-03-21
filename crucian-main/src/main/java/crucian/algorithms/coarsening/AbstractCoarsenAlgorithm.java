package crucian.algorithms.coarsening;

import vnreal.algorithms.AbstractAlgorithm;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

import java.util.Iterator;

public abstract class AbstractCoarsenAlgorithm extends AbstractAlgorithm {

    private NetworkStack networkStack;
    private Iterator currentVNIt;

    protected AbstractCoarsenAlgorithm(NetworkStack networkStack) {
        this.networkStack = networkStack;
        currentVNIt = networkStack.iterator();
        if (currentVNIt != null && currentVNIt.hasNext()) {
            currentVNIt.next();
        }
    }

    @Override
    protected void evaluate() {
        while (hasNext()) {
            VirtualNetwork coarsened = coarsen(next());
            if (process(coarsened)) {
                undoCoarsen(coarsened);
            }
        }
    }

    protected boolean hasNext() {
        return currentVNIt != null && currentVNIt.hasNext();
    }

    protected VirtualNetwork next() {
        return (VirtualNetwork) currentVNIt.next();
    }

    protected SubstrateNetwork getSubstrateNetwork() {
        return networkStack.getSubstrate();
    }

    protected abstract boolean process(VirtualNetwork graph);

    protected abstract VirtualNetwork coarsen(VirtualNetwork toCoarsen);

    protected abstract VirtualNetwork undoCoarsen(VirtualNetwork coarsened);
}
