package crucian.benchmark.generators;

import crucian.benchmark.io.GtItmVirtualNetworkLoader;
import vnreal.demands.BandwidthDemand;
import vnreal.demands.CpuDemand;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

/**
 * Created at 14-3-16 下午3:49.
 *
 * @author lirui
 */
public class RandomGtItmVirtualNetworkGenerator extends RandomGtItmNetworkGenerator<VirtualNetwork> {
    public RandomGtItmVirtualNetworkGenerator(int minNodeCount, int maxNodeCount, double connectProbability, double minResource, double maxResource) {
        super(minNodeCount, maxNodeCount, connectProbability, minResource, maxResource);
    }

    @Override
    protected VirtualNetwork loadFromFile(String graphFile) throws Exception {
        VirtualNetwork virtualNetwork = (VirtualNetwork) new GtItmVirtualNetworkLoader().loadFromFile(graphFile);
        for (VirtualNode virtualNode : virtualNetwork.getVertices()) {
            CpuDemand cpuDemand = new CpuDemand(virtualNode);
            cpuDemand.setDemandedCycles(resource());
            virtualNode.add(cpuDemand);
        }

        for (VirtualLink virtualLink : virtualNetwork.getEdges()) {
            BandwidthDemand bandwidthDemand = new BandwidthDemand(virtualLink);
            bandwidthDemand.setDemandedBandwidth(resource());
            virtualLink.add(bandwidthDemand);
        }

        return virtualNetwork;
    }
}
