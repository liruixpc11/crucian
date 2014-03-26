package crucian.benchmark.algorithms;

import crucian.benchmark.AbstractMappingAlgorithm;
import crucian.benchmark.AlgorithmAction;
import crucian.benchmark.AlgorithmFactory;
import crucian.benchmark.MappingAlgorithm;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

/**
 * Created at 14-3-23 上午11:50.
 *
 * @author lirui
 */
@AlgorithmAction("模拟退火")
public class SimulatedAnnealMappingAlgorithm extends AbstractMappingAlgorithm {

    @AlgorithmFactory
    public static SimulatedAnnealMappingAlgorithm create() {
        SimulatedAnnealMappingAlgorithm algorithm = new SimulatedAnnealMappingAlgorithm();
        return algorithm;
    }

    @Override
    public void map(SubstrateNetwork substrateNetwork, VirtualNetwork virtualNetwork) throws Exception {
    }
}
