package crucian.algorithms.coarsening;

import vnreal.constraints.AbstractConstraint;
import vnreal.constraints.INodeConstraint;
import vnreal.demands.AbstractDemand;
import vnreal.demands.BandwidthDemand;
import vnreal.demands.DemandVisitorAdapter;
import vnreal.network.NetworkEntity;
import vnreal.resources.AbstractResource;

/**
 * 本地带宽，默认无限大小
 */
public class LocalBandwidthResource extends AbstractResource implements INodeConstraint {

    protected LocalBandwidthResource(NetworkEntity<? extends AbstractConstraint> ne) {
        super(ne);
    }

    @Override
    protected DemandVisitorAdapter createOccupyVisitor() {
        return new DemandVisitorAdapter() {
            @Override
            public boolean visit(BandwidthDemand dem) {
                return true;
            }
        };
    }

    @Override
    protected DemandVisitorAdapter createFreeVisitor() {
        return new DemandVisitorAdapter() {
            @Override
            public boolean visit(BandwidthDemand dem) {
                return true;
            }
        };
    }

    @Override
    public boolean accepts(AbstractDemand dem) {
        return dem.getClass() == BandwidthDemand.class;
    }

    @Override
    public boolean fulfills(AbstractDemand dem) {
        return true;
    }

    @Override
    public AbstractResource getCopy(NetworkEntity<? extends AbstractConstraint> owner) {
        return new LocalBandwidthResource(owner);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<unlimited>";
    }
}
