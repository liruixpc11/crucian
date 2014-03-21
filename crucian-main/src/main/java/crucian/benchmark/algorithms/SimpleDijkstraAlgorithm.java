package crucian.benchmark.algorithms;

import crucian.benchmark.AbstractMappingAlgorithm;
import crucian.benchmark.AlgorithmAction;
import crucian.benchmark.AlgorithmFactory;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

import java.util.Collections;

/**
 * @author LiRuiNet
 *         14-3-1 上午11:24
 */
@AlgorithmAction("Simple Dijkstra")
public class SimpleDijkstraAlgorithm extends AbstractMappingAlgorithm {
    @AlgorithmFactory
    public static SimpleDijkstraAlgorithm create() {
        SimpleDijkstraAlgorithm algorithm = new SimpleDijkstraAlgorithm();
        return algorithm;
    }

    @Override
    public void map(SubstrateNetwork substrateNetwork, VirtualNetwork virtualNetwork) throws Exception {
        NetworkStack networks = new NetworkStack(substrateNetwork, Collections.singletonList(virtualNetwork));
        vnreal.algorithms.samples.SimpleDijkstraAlgorithm algorithm = new vnreal.algorithms.samples.SimpleDijkstraAlgorithm(networks);
        algorithm.performEvaluation();
    }
}
