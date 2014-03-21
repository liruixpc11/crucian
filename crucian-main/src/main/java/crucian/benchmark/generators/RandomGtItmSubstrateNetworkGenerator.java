package crucian.benchmark.generators;

import crucian.benchmark.io.GtItmSubstrateNetworkLoader;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.resources.BandwidthResource;
import vnreal.resources.CpuResource;

/**
 * Created at 14-3-16 下午4:06.
 *
 * @author lirui
 */
public class RandomGtItmSubstrateNetworkGenerator extends RandomGtItmNetworkGenerator<SubstrateNetwork> {
    public RandomGtItmSubstrateNetworkGenerator(int minNodeCount, int maxNodeCount, double connectProbability, double minResource, double maxResource) {
        super(minNodeCount, maxNodeCount, connectProbability, minResource, maxResource);
    }

    @Override
    protected SubstrateNetwork loadFromFile(String graphFile) throws Exception {
        SubstrateNetwork substrateNetwork = (SubstrateNetwork) new GtItmSubstrateNetworkLoader().loadFromFile(graphFile);
        for (SubstrateNode substrateNode : substrateNetwork.getVertices()) {
            CpuResource cpuResource = new CpuResource(substrateNode);
            cpuResource.setCycles(resource());
            substrateNode.add(cpuResource);
        }

        for (SubstrateLink substrateLink : substrateNetwork.getEdges()) {
            BandwidthResource bandwidthResource = new BandwidthResource(substrateLink);
            bandwidthResource.setBandwidth(resource());
            substrateLink.add(bandwidthResource);
        }

        return substrateNetwork;
    }
}
