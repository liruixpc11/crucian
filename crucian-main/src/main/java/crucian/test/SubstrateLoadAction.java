package crucian.test;

import crucian.benchmark.io.BriteSubstrateNetworkLoader;
import vnreal.ToolKit;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNetwork;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created at 14-3-4 上午10:47.
 *
 * @author lirui
 */
public class SubstrateLoadAction extends AbstractAction {
    public SubstrateLoadAction() {
        super("加载底层网络");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("/home/lirui/tmp/test.brite");
            SubstrateNetwork substrateNetwork = (SubstrateNetwork) new BriteSubstrateNetworkLoader().load(inputStream);
            NetworkStack networks = ToolKit.getScenario().getNetworkStack();
            ToolKit.getScenario().setNetworkStack(new NetworkStack(substrateNetwork, networks.getVirtualNetworks()));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
