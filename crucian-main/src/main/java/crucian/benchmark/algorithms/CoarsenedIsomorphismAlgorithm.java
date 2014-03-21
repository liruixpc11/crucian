package crucian.benchmark.algorithms;

import crucian.algorithms.coarsening.BasicCoarsening;
import crucian.algorithms.coarsening.VirtualNetworkCoarsening;
import crucian.benchmark.AlgorithmAction;
import crucian.benchmark.AlgorithmFactory;
import crucian.benchmark.AlgorithmParam;
import crucian.benchmark.DefaultValue;
import vnreal.algorithms.isomorphism.SubgraphIsomorphismAlgorithm;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

/**
 * @author LiRuiNet
 *         14-3-2 下午9:34
 */
@AlgorithmAction("Coarsened Isomorphism")
public class CoarsenedIsomorphismAlgorithm extends BasicCoarsenedMappingAlgorithm {
    private boolean useLog;

    @Override
    protected VirtualNetworkCoarsening getCoarsening() {
        BasicCoarsening coarsening = new BasicCoarsening();
        coarsening.useLog(useLog);
        return coarsening;
    }

    @AlgorithmFactory
    public static CoarsenedIsomorphismAlgorithm create(@AlgorithmParam("是否输出日志") @DefaultValue("True") boolean useLog) {
        CoarsenedIsomorphismAlgorithm algorithm = new CoarsenedIsomorphismAlgorithm();
        algorithm.useLog = useLog;
        return algorithm;
    }

    @Override
    protected void doMap(SubstrateNetwork substrateNetwork, VirtualNetwork virtualNetwork) throws Exception {
        SubgraphIsomorphismAlgorithm algorithm = new SubgraphIsomorphismAlgorithm(false);
        if (!algorithm.mapNetwork(substrateNetwork, virtualNetwork)) {
            throw new Exception("failed");
        }
    }
}
