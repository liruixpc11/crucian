package crucian.benchmark.io;

import vnreal.constraints.AbstractConstraint;
import vnreal.network.Link;
import vnreal.network.Network;
import vnreal.network.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Created at 14-3-4 下午4:29.
 *
 * @author lirui
 */
public abstract class GtItmNetworkLoader<T extends AbstractConstraint, V extends Node<T>, L extends Link<T>> extends AbstractNetworkLoader<T, V, L> {
    protected abstract V createNode(Network<T, V, L> network);

    protected abstract L createLink(Network<T, V, L> network);

    private static enum Stage {
        Init,
        Nodes,
        Links
    }

    @Override
    protected void process(String content, Network<T, V, L> network) {
        for (String line : content.split("\n")) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            if (line.startsWith("VERTICES")) {
                stage = Stage.Nodes;
            } else if (line.startsWith("EDGES")) {
                stage = Stage.Links;
            } else {
                switch (stage) {
                    case Nodes:
                        handleNode(line, network);
                        break;
                    case Links:
                        handleLink(line, network);
                        break;
                }
            }
        }
    }

    protected void handleNode(String line, Network<T, V, L> network) {
        String[] parts = line.split("\\s+");
        V node = createNode(network);
        if (parts.length >= 4) {
            node.setCoordinateX(Double.parseDouble(parts[2]));
            node.setCoordinateY(Double.parseDouble(parts[3]));
        }
        vMap.put(parts[0], node);
        network.addVertex(node);
    }

    protected void handleLink(String line, Network<T, V, L> network) {
        String[] parts = line.split("\\s+");
        L link = createLink(network);
        network.addEdge(link, vMap.get(parts[0]), vMap.get(parts[1]));
    }

    private Stage stage = Stage.Init;
    private Map<String, V> vMap = new HashMap<String, V>();
}
