package crucian.benchmark.generators;

import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.resources.BandwidthResource;
import vnreal.resources.CpuResource;

/**
 * Created at 14-3-4 下午7:06.
 *
 * @author lirui
 */
public abstract class RandomGtItmNetworkGenerator<N> extends BaseGtItmNetworkGenerator<N> {
    private double connectProbability;

    public RandomGtItmNetworkGenerator(int minNodeCount, int maxNodeCount, double connectProbability, double minResource, double maxResource) {
        super(minNodeCount, maxNodeCount, minResource, maxResource);
        this.connectProbability = connectProbability;
    }

    @Override
    protected String configFileContent() {
        int nodeCount = nodeCount();
        // int n = Math.max((int) Math.round(Math.sqrt(nodeCount)), 2);
        int n = Math.max(nodeCount, 2);
        int m = Math.max(nodeCount, 2);
        String content = "geo 1\n" + String.format("%d %d 3 %.4f", n, m, connectProbability);
        return content;
    }

    public static void main(String[] args) throws Exception {
        SubstrateNetwork virtualNetwork = new RandomGtItmSubstrateNetworkGenerator(10, 10, 0.1, 1, 10).create();
        System.out.println(virtualNetwork.getVertexCount());
        System.out.println(virtualNetwork.getEdgeCount());
        int i = 1;
        for (SubstrateNode virtualNode : virtualNetwork.getVertices()) {
            System.out.println((i++) + ": " + virtualNode.getSingle(CpuResource.class).getAvailableCycles());
        }
        i = 1;
        for (SubstrateLink virtualLink : virtualNetwork.getEdges()) {
            System.out.println((i++) + ": " + virtualLink.getSingle(BandwidthResource.class).getAvailableBandwidth());
        }
    }
}
