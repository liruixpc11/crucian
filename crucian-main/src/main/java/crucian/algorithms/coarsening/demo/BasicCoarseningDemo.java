package crucian.algorithms.coarsening.demo;

import crucian.algorithms.coarsening.BasicCoarsening;
import vnreal.ToolKit;
import vnreal.io.ScenarioImporter;
import vnreal.network.NetworkStack;
import vnreal.network.virtual.VirtualNetwork;

public class BasicCoarseningDemo {
    public static void main(String[] args) throws Exception {
        new ScenarioImporter("src/XML/big-scenario-high-load.xml").setNetworkStack();
        NetworkStack networks = ToolKit.getScenario().getNetworkStack();
        System.out.println("substrate: " + networks.getSubstrate().getVertexCount());
        VirtualNetwork originalVirtualNetwork = (VirtualNetwork) networks.getLayer(1);
        VirtualNetwork network = new BasicCoarsening().coarsen(originalVirtualNetwork, networks.getSubstrate());
        System.out.println("original: " + originalVirtualNetwork.getVertexCount());
        System.out.println("coarsened: " + network.getVertexCount());
    }
}
