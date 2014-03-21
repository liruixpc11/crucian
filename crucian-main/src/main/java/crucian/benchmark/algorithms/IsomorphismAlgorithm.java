package crucian.benchmark.algorithms;

import crucian.benchmark.AbstractMappingAlgorithm;
import crucian.benchmark.AlgorithmAction;
import crucian.benchmark.AlgorithmFactory;
import vnreal.algorithms.isomorphism.SubgraphIsomorphismAlgorithm;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

/**
 * @author LiRuiNet
 *         14-3-1 上午11:46
 */
@AlgorithmAction("Isomorphism")
public class IsomorphismAlgorithm extends AbstractMappingAlgorithm {
    @AlgorithmFactory
    public static IsomorphismAlgorithm create() {
        IsomorphismAlgorithm algorithm = new IsomorphismAlgorithm();
        return algorithm;
    }

    @Override
    public void map(SubstrateNetwork substrateNetwork, VirtualNetwork virtualNetwork) throws Exception {
        SubgraphIsomorphismAlgorithm algorithm = new SubgraphIsomorphismAlgorithm(false);
        if (!algorithm.mapNetwork(substrateNetwork, virtualNetwork)) {
            throw new Exception("failed");
        }
    }
}
