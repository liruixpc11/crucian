package crucian.benchmark.generators;

import crucian.benchmark.RandomUtility;
import crucian.benchmark.io.BriteVirtualNetworkLoader;
import vnreal.demands.CpuDemand;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

/**
 * Created at 14-3-4 上午11:05.
 *
 * @author lirui
 */
public class BriteFileVirtualNetworkGenerator implements NetworkGenerator<VirtualNetwork> {
    private RandomUtility randomUtility = new RandomUtility();
    private String briteFileName;
    private double minResource;
    private double maxResource;

    public BriteFileVirtualNetworkGenerator(String briteFileName, double minResource, double maxResource) {
        this.briteFileName = briteFileName;
        this.minResource = minResource;
        this.maxResource = maxResource;
    }

    @Override
    public VirtualNetwork create() {
        try {
            VirtualNetwork virtualNetwork = (VirtualNetwork) new BriteVirtualNetworkLoader().loadFromFile(briteFileName);
            for (VirtualNode virtualNode : virtualNetwork.getVertices()) {
                CpuDemand cpuDemand = new CpuDemand(virtualNode);
                cpuDemand.setDemandedCycles(resource());
                virtualNode.add(cpuDemand);
            }

            return virtualNetwork;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private double resource() {
        return randomUtility.nextDouble() * (maxResource - minResource) + minResource;
    }
}
