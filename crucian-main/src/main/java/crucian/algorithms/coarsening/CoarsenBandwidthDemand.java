package crucian.algorithms.coarsening;

import vnreal.constraints.AbstractConstraint;
import vnreal.demands.BandwidthDemand;
import vnreal.network.Link;

import java.util.ArrayList;
import java.util.List;

public class CoarsenBandwidthDemand extends BandwidthDemand implements CoarsenedDemand<BandwidthDemand> {
    private List<BandwidthDemand> bandwidthDemands = new ArrayList<BandwidthDemand>();

    public CoarsenBandwidthDemand(Link<? extends AbstractConstraint> owner) {
        super(owner);
    }

    @Override
    public void addDemand(BandwidthDemand subDemand) {
        if (subDemand instanceof CoarsenBandwidthDemand) {
            for (BandwidthDemand demand : ((CoarsenBandwidthDemand) subDemand).getDemands()) {
                addDemand(demand);
            }
        } else {
            bandwidthDemands.add(subDemand);
            setDemandedBandwidth(getDemandedBandwidth() + subDemand.getDemandedBandwidth());
        }
    }

    @Override
    public List<BandwidthDemand> getDemands() {
        return bandwidthDemands;
    }
}
