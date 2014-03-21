package crucian.algorithms.coarsening;

import vnreal.demands.AbstractDemand;
import vnreal.network.NetworkEntity;

import java.util.List;

public interface CoarsenEntity<E extends NetworkEntity<AbstractDemand>> {
    public void addSub(E sub);

    public List<E> getAllSub();

    public List<AbstractDemand> get();
}
