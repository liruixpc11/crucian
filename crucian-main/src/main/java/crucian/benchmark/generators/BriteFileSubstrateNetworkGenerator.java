package crucian.benchmark.generators;

import crucian.benchmark.RandomUtility;
import crucian.benchmark.io.BriteSubstrateNetworkLoader;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.resources.CpuResource;

/**
 * Created at 14-3-3 下午4:42.
 *
 * @author lirui
 */
public class BriteFileSubstrateNetworkGenerator implements NetworkGenerator<SubstrateNetwork> {
    private RandomUtility randomUtility = new RandomUtility();
    private double minResource;
    private double maxResource;
    private String briteFileName;

    public BriteFileSubstrateNetworkGenerator(double minResource, double maxResource, String briteFileName) {
        this.minResource = minResource;
        this.maxResource = maxResource;
        this.briteFileName = briteFileName;
    }

    @Override
    public SubstrateNetwork create() {
        try {
            SubstrateNetwork substrateNetwork = (SubstrateNetwork) new BriteSubstrateNetworkLoader().loadFromFile(briteFileName);
            for (SubstrateNode substrateNode : substrateNetwork.getVertices()) {
                CpuResource cpuResource = new CpuResource(substrateNode);
                cpuResource.setCycles(resource());
                substrateNode.add(cpuResource);
            }

            return substrateNetwork;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private double resource() {
        return randomUtility.nextDouble() * (maxResource - minResource) + minResource;
    }
}
