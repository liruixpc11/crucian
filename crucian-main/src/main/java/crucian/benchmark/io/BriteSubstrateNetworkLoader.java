package crucian.benchmark.io;

import vnreal.network.Network;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.BandwidthResource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created at 14-3-4 上午10:42.
 *
 * @author lirui
 */
public class BriteSubstrateNetworkLoader extends BriteNetworkLoader<AbstractResource, SubstrateNode, SubstrateLink> {
    @Override
    protected SubstrateNode createNode(Network<AbstractResource, SubstrateNode, SubstrateLink> network) {
        return new SubstrateNode();
    }

    @Override
    protected SubstrateLink createLink(Network<AbstractResource, SubstrateNode, SubstrateLink> network) {
        return new SubstrateLink();
    }

    @Override
    protected AbstractResource createBandwidthConstraint(SubstrateLink link, double bandwidth) {
        BandwidthResource bandwidthResource = new BandwidthResource(link);
        bandwidthResource.setBandwidth(bandwidth);
        return bandwidthResource;
    }

    @Override
    protected Network<AbstractResource, SubstrateNode, SubstrateLink> createNetwork() {
        return new SubstrateNetwork();
    }

    public static void main(String[] args) throws Exception {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("/home/lirui/tmp/test.brite");
            SubstrateNetwork substrateNetwork = (SubstrateNetwork) new BriteSubstrateNetworkLoader().load(inputStream);
            System.out.println(substrateNetwork.toString());
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }
}
