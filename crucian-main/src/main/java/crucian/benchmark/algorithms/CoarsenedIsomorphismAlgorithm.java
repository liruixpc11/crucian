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
    private boolean useAverageThreshold;
    private boolean supportCluster;

    @Override
    protected VirtualNetworkCoarsening createCoarsening() {
        BasicCoarsening coarsening = new BasicCoarsening();
        coarsening.useLog(useLog);
        coarsening.setUseAverageThreshold(useAverageThreshold);
        coarsening.setSupportCluster(supportCluster);
        return coarsening;
    }

    @AlgorithmFactory
    public static CoarsenedIsomorphismAlgorithm create(
            @AlgorithmParam("是否输出日志") @DefaultValue("False") boolean useLog,
            @AlgorithmParam("是否使用平均值作为阈值") @DefaultValue("False") boolean useAverageThreshold,
            @AlgorithmParam("是否支持聚合粗化") @DefaultValue("False") boolean supportCluster
    ) {
        CoarsenedIsomorphismAlgorithm algorithm = new CoarsenedIsomorphismAlgorithm();
        algorithm.useLog = useLog;
        algorithm.useAverageThreshold = useAverageThreshold;
        algorithm.supportCluster = supportCluster;
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
