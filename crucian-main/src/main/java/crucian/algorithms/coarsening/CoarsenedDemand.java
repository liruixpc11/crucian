package crucian.algorithms.coarsening;

import vnreal.demands.AbstractDemand;
import vnreal.mapping.Mapping;

import java.util.List;

public interface CoarsenedDemand<D extends AbstractDemand> {
    public void addDemand(D demand);

    public List<D> getDemands();

    public List<Mapping> getMappings();
}
