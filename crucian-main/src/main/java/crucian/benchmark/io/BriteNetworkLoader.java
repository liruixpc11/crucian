package crucian.benchmark.io;

import vnreal.constraints.AbstractConstraint;
import vnreal.network.Link;
import vnreal.network.Network;
import vnreal.network.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Created at 14-3-4 上午9:39.
 *
 * @author lirui
 */
public abstract class BriteNetworkLoader<T extends AbstractConstraint, V extends Node<T>, L extends Link<T>> extends AbstractNetworkLoader<T, V, L> {
    protected abstract V createNode(Network<T, V, L> network);

    protected abstract L createLink(Network<T, V, L> network);

    protected abstract T createBandwidthConstraint(L link, double bandwidth);

    private static enum Stage {
        Init,
        Nodes,
        Links
    }

    @Override
    protected final void process(String content, Network<T, V, L> network) {
        for (String line : content.split("\n")) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            if (line.startsWith("Nodes:")) {
                stage = Stage.Nodes;
            } else if (line.startsWith("Edges:")) {
                stage = Stage.Links;
            } else {
                switch (stage) {
                    case Nodes:
                        handleNodeLine(line, network);
                        break;
                    case Links:
                        handleLinkLine(line, network);
                        break;
                }
            }
        }
    }

    private void handleNodeLine(String line, Network<T, V, L> network) {
        // NodeID x-coord y-coord inDegree outDegree ASid type
        String[] parts = line.split("\\s");
        V node = createNode(network);
        nodeMap.put(parts[0], node);
        node.setCoordinateX(Double.parseDouble(parts[1]));
        node.setCoordinateY(Double.parseDouble(parts[2]));
    }

    private void handleLinkLine(String line, Network<T, V, L> network) {
        // EdgeID fromNodeID toNodeID Length Delay Bandwidth ASFromNodeID ASToNodeID EdgeType Direction
        String[] parts = line.split("\\s");
        L link = createLink(network);
        linkMap.put(parts[0], link);
        link.add(createBandwidthConstraint(link, Double.parseDouble(parts[5])));
        network.addEdge(link, nodeMap.get(parts[1]), nodeMap.get(parts[2]));
    }

    private Stage stage = Stage.Init;
    private Map<String, V> nodeMap = new HashMap<String, V>();
    private Map<String, L> linkMap = new HashMap<String, L>();
}
