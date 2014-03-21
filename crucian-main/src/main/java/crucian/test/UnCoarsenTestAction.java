package crucian.test;

import crucian.algorithms.coarsening.BasicCoarsening;
import crucian.algorithms.coarsening.VirtualNetworkCoarsening;
import vnreal.ToolKit;
import vnreal.network.NetworkStack;
import vnreal.network.virtual.VirtualNetwork;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author LiRuiNet
 *         14-3-2 下午10:40
 */
public class UnCoarsenTestAction extends AbstractAction {
    public UnCoarsenTestAction() {
        super("粗化-反粗化测试");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        NetworkStack networkStack = ToolKit.getScenario().getNetworkStack();
        if (networkStack == null) return;
        VirtualNetwork originalNetwork = (VirtualNetwork) networkStack.getLayer(1);
        VirtualNetworkCoarsening coarsening = new BasicCoarsening();
        VirtualNetwork virtualNetwork = coarsening.coarsen(originalNetwork, networkStack.getSubstrate());
        virtualNetwork = coarsening.undoCoarsen(virtualNetwork);
    }
}
