package crucian.algorithms.coarsening;

import vnreal.constraints.AbstractConstraint;
import vnreal.network.Link;
import vnreal.resources.BandwidthResource;

import java.util.ArrayList;
import java.util.List;

public class CoarsenBandwidthResource extends BandwidthResource implements CoarsenedResource<BandwidthResource> {
    private final List<BandwidthResource> resources = new ArrayList<BandwidthResource>();

    public CoarsenBandwidthResource(Link<? extends AbstractConstraint> owner) {
        super(owner);
    }

    @Override
    public void addResource(BandwidthResource resource) {
        setBandwidth(getBandwidth() + resource.getBandwidth());
        setOccupiedBandwidth(getOccupiedBandwidth() + resource.getOccupiedBandwidth());
        resources.add(resource);
    }

    @Override
    public List<BandwidthResource> getResources() {
        return resources;
    }
}
