package crucian.algorithms.coarsening;

import vnreal.demands.BandwidthDemand;
import vnreal.network.virtual.VirtualLink;

import java.util.ArrayList;
import java.util.List;

public class CoarsenVirtualLink extends VirtualLink implements CoarsenEntity<VirtualLink> {
    private final List<VirtualLink> virtualLinks = new ArrayList<VirtualLink>();
    private CoarsenBandwidthDemand coarsenBandwidthDemand = new CoarsenBandwidthDemand(this);
    private int coarsenLevel;

    public CoarsenVirtualLink(int id, int layer) {
        super(id, layer);
        add(coarsenBandwidthDemand);
    }

    public CoarsenVirtualLink(int id, VirtualLink[] virtualLinks) {
        this(id, virtualLinks[0].getLayer());
        for (VirtualLink virtualLink : virtualLinks) {
            addSub(virtualLink);
        }
    }

    @Override
    public void addSub(VirtualLink virtualLink) {
        virtualLinks.add(virtualLink);
        for (BandwidthDemand demand : virtualLink.get(BandwidthDemand.class)) {
            coarsenBandwidthDemand.addDemand(demand);
        }
    }

    @Override
    public List<VirtualLink> getAllSub() {
        return virtualLinks;
    }

    public int getCoarsenLevel() {
        return coarsenLevel;
    }

    public void setCoarsenLevel(int coarsenLevel) {
        this.coarsenLevel = coarsenLevel;
    }

    @Override
    public String toString() {
        return "CoarsenVirtualLink{" + super.toString() + "}";
    }
}
