package crucian.benchmark.generators;

import crucian.benchmark.RandomUtility;
import vnreal.demands.BandwidthDemand;
import vnreal.demands.CpuDemand;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

/**
 * Created at 14-3-3 下午4:42.
 *
 * @author lirui
 */
public class BasicVirtualNetworkGenerator implements NetworkGenerator<VirtualNetwork> {
    private RandomUtility randomUtility = new RandomUtility();
    private int minNodeCount;
    private int maxNodeCount;
    private double connectProbability;
    private double minResource;
    private double maxResource;

    public BasicVirtualNetworkGenerator(int minNodeCount,
                                        int maxNodeCount,
                                        double connectProbability,
                                        double minResource,
                                        double maxResource) {
        this.minNodeCount = minNodeCount;
        this.maxNodeCount = maxNodeCount;
        this.connectProbability = connectProbability;
        this.minResource = minResource;
        this.maxResource = maxResource;
    }

    @Override
    public VirtualNetwork create() {
        VirtualNetwork virtualNetwork = new VirtualNetwork(1);
        int nodeCount = randomUtility.nextInt(maxNodeCount - minNodeCount) + minNodeCount;
        for (int i = 0; i < nodeCount; i++) {
            VirtualNode virtualNode = new VirtualNode(virtualNetwork.getLayer());
            CpuDemand cpuDemand = new CpuDemand(virtualNode);
            cpuDemand.setDemandedCycles(resource());
            virtualNode.add(cpuDemand);
            virtualNetwork.addVertex(virtualNode);
        }

        for (VirtualNode v : virtualNetwork.getVertices()) {
            for (VirtualNode w : virtualNetwork.getVertices()) {
                if (v == w) continue;

                if (randomUtility.nextDouble() < connectProbability) {
                    VirtualLink virtualLink = new VirtualLink(virtualNetwork.getLayer());
                    BandwidthDemand bandwidthDemand = new BandwidthDemand(virtualLink);
                    bandwidthDemand.setDemandedBandwidth(resource());
                    virtualLink.add(bandwidthDemand);
                    virtualNetwork.addEdge(virtualLink, v, w);
                }
            }
        }

        return virtualNetwork;
    }

    private double resource() {
        return randomUtility.nextDouble() * (maxResource - minResource) + minResource;
    }
}
