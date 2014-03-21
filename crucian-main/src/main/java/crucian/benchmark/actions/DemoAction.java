package crucian.benchmark.actions;

import crucian.benchmark.AbstractMappingAlgorithm;
import crucian.benchmark.ConsoleReporter;
import crucian.benchmark.benchmarks.MappingTimeBenchmark;
import vnreal.ToolKit;
import vnreal.gui.GUI;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author LiRuiNet
 *         14-2-28 下午3:28
 */
public class DemoAction extends AbstractAction {
    public DemoAction() {
        super("简单测试");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        NetworkStack networks = ToolKit.getScenario().getNetworkStack();
        if (networks == null) {
            JOptionPane.showMessageDialog(GUI.getInstance(), "网络栈为空，请加载", "出错", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new MappingTimeBenchmark(10).setScenario(networks).evaluate(new AbstractMappingAlgorithm() {
            @Override
            public void map(SubstrateNetwork substrateNetwork, VirtualNetwork virtualNetwork) throws Exception {
                Thread.sleep(1);
            }
        }).reportStatus(new ConsoleReporter());
    }
}
