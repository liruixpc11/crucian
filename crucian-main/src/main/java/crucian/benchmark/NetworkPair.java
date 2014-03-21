package crucian.benchmark;

import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

/**
 * Created at 14-3-18 下午9:59.
 *
 * @author lirui
 */
public class NetworkPair {
    private SubstrateNetwork substrateNetwork;
    private VirtualNetwork virtualNetwork;

    public NetworkPair(SubstrateNetwork substrateNetwork, VirtualNetwork virtualNetwork) {
        this.substrateNetwork = substrateNetwork;
        this.virtualNetwork = virtualNetwork;
    }

    public SubstrateNetwork getSubstrateNetwork() {
        return substrateNetwork;
    }

    public VirtualNetwork getVirtualNetwork() {
        return virtualNetwork;
    }
}
