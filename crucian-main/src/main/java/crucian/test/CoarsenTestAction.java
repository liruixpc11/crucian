package crucian.test;

import crucian.algorithms.coarsening.BasicCoarsening;
import vnreal.ToolKit;
import vnreal.network.NetworkStack;
import vnreal.network.virtual.VirtualNetwork;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

/**
 * @author LiRuiNet
 *         14-2-20 下午7:19
 */
public class CoarsenTestAction extends AbstractAction {
    public CoarsenTestAction() {
        super("粗化测试");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        NetworkStack networkStack = ToolKit.getScenario().getNetworkStack();
        if (networkStack == null) return;
        VirtualNetwork originalNetwork = (VirtualNetwork) networkStack.getLayer(1);
        VirtualNetwork virtualNetwork = new BasicCoarsening().coarsen(originalNetwork, networkStack.getSubstrate());
        ToolKit.getScenario().setNetworkStack(new NetworkStack(networkStack.getSubstrate(), Arrays.asList(originalNetwork, virtualNetwork)));
    }
}
