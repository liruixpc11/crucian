package crucian.benchmark.algorithms;

import crucian.benchmark.AbstractMappingAlgorithm;
import crucian.benchmark.AlgorithmAction;
import crucian.benchmark.AlgorithmFactory;
import crucian.benchmark.AlgorithmParam;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

/**
 * @author LiRuiNet
 *         14-2-28 下午7:55
 */
@AlgorithmAction("示例")
public class DemoAlgorithm extends AbstractMappingAlgorithm {
    private int sleepMilliseconds = 1;
    private boolean useError = false;
    private int firstErrorIndex;
    private int errorInterval;
    private int currentIndex = 0;

    @Override
    public void map(SubstrateNetwork substrateNetwork, VirtualNetwork virtualNetwork) throws Exception {
        currentIndex++;
        if (useError && currentIndex >= firstErrorIndex) {
            if ((currentIndex - firstErrorIndex) % errorInterval == 0) {
                throw new Exception(Integer.toString(currentIndex));
            }
        }
        Thread.sleep(sleepMilliseconds);
    }

    @AlgorithmFactory
    public static DemoAlgorithm create(
            @AlgorithmParam("睡眠毫秒数") int sleepMilliseconds,
            @AlgorithmParam("是否抛出错误") boolean useError,
            @AlgorithmParam("第一次错误时间") int firstErrorIndex,
            @AlgorithmParam("出错间隔") int errorInterval
    ) {
        DemoAlgorithm algorithm = new DemoAlgorithm();
        algorithm.sleepMilliseconds = sleepMilliseconds;
        algorithm.useError = useError;
        algorithm.firstErrorIndex = firstErrorIndex;
        algorithm.errorInterval = errorInterval;
        return algorithm;
    }
}
