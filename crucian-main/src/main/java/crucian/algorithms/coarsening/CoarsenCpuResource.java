package crucian.algorithms.coarsening;

import vnreal.constraints.AbstractConstraint;
import vnreal.network.Node;
import vnreal.resources.CpuResource;

import java.util.ArrayList;
import java.util.List;

public class CoarsenCpuResource extends CpuResource implements CoarsenedResource<CpuResource> {
    private final List<CpuResource> resources = new ArrayList<CpuResource>();

    public CoarsenCpuResource(Node<? extends AbstractConstraint> owner) {
        super(owner);
    }

    @Override
    public void addResource(CpuResource resource) {
        setCycles(getCycles() + resource.getCycles());
        setOccupiedCycles(getOccupiedCycles() + resource.getOccupiedCycles());
        resources.add(resource);
    }

    @Override
    public List<CpuResource> getResources() {
        return resources;
    }
}
