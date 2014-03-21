package crucian.benchmark.algorithms;

import crucian.benchmark.AbstractMappingAlgorithm;
import crucian.benchmark.AlgorithmAction;
import crucian.benchmark.AlgorithmFactory;
import vnreal.algorithms.CoordinatedMappingPathSplitting;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

import java.util.Collections;

/**
 * @author LiRuiNet
 *         14-3-1 上午11:15
 */
@AlgorithmAction("Coordinated")
public class CoordinatedMappingAlgorithm extends AbstractMappingAlgorithm {
    @AlgorithmFactory
    public static CoordinatedMappingAlgorithm create() {
        CoordinatedMappingAlgorithm algorithm = new CoordinatedMappingAlgorithm();
        return algorithm;
    }

    @Override
    public void map(SubstrateNetwork substrateNetwork, VirtualNetwork virtualNetwork) throws Exception {
        NetworkStack networks = new NetworkStack(substrateNetwork, Collections.singletonList(virtualNetwork));
        CoordinatedMappingPathSplitting algorithm = new CoordinatedMappingPathSplitting(
                networks, 20, 1, 1, 1, false);
        algorithm.performEvaluation();
    }
}
