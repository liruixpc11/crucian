package crucian.test;

import crucian.benchmark.generators.BasicSubstrateNetworkGenerator;
import crucian.benchmark.generators.NetworkGenerator;
import crucian.benchmark.generators.WaxmanVirtualNetworkGenerator;
import vnreal.ToolKit;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created at 14-3-3 下午7:20.
 *
 * @author lirui
 */
public class NetworkGenerationAction extends AbstractAction {
    public NetworkGenerationAction() {
        super("网络生成测试");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        BasicSubstrateNetworkGenerator substrateNetworkGenerator = new BasicSubstrateNetworkGenerator();
        SubstrateNetwork substrateNetwork = substrateNetworkGenerator.create();
        // BasicVirtualNetworkGenerator generator = new BasicVirtualNetworkGenerator(2, 200, 0.2, 0, 20);
        NetworkGenerator<VirtualNetwork> generator = new WaxmanVirtualNetworkGenerator(2, 200, 0.2, 0, 20);
        List<VirtualNetwork> virtualNetworks = new ArrayList<VirtualNetwork>();
        for (int i = 0; i < 8; i++) {
            virtualNetworks.add(generator.create());
        }
        ToolKit.getScenario().setNetworkStack(new NetworkStack(substrateNetwork, virtualNetworks));
    }
}
