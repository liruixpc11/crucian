package crucian.benchmark.generators;

import vnreal.ToolKit;
import vnreal.network.substrate.SubstrateNetwork;

/**
 * Created at 14-3-3 下午4:42.
 *
 * @author lirui
 */
public class BasicSubstrateNetworkGenerator implements NetworkGenerator<SubstrateNetwork> {
    public BasicSubstrateNetworkGenerator() {
    }

    @Override
    public SubstrateNetwork create() {
        return ToolKit.getScenario().getNetworkStack().getSubstrate();
    }
}
