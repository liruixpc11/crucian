package crucian.benchmark.benchmarks;

import crucian.benchmark.*;
import vnreal.network.virtual.VirtualNetwork;

/**
 * @author LiRuiNet
 *         14-2-28 上午9:45
 */

@BenchmarkAction("时间")
public class MappingTimeBenchmark extends AbstractBenchmark {
    private final int times;

    public MappingTimeBenchmark(int times) {
        this.times = times;
    }

    @BenchmarkFactory
    public static MappingTimeBenchmark create(
            @BenchmarkParam("次数") @DefaultValue("1") int times
    ) {
        return new MappingTimeBenchmark(times);
    }

    @Override
    public void doEvaluate(MappingAlgorithm mappingAlgorithm) {
        int errorCount = 0;
        int successCount = 0;
        long startNano = System.nanoTime();
        for (int i = 0; i < times; i++) {
            clearScenario();
            for (VirtualNetwork virtualNetwork : getScenario().getVirtualNetworks()) {
                try {
                    mappingAlgorithm.map(getScenario().getSubstrate(), virtualNetwork);
                    successCount++;
                } catch (Exception e) {
                    e.printStackTrace();
                    errorCount++;
                }
            }
        }

        long total = System.nanoTime() - startNano;
        putStatus("总时间（ns）", total);
        putStatus("错误次数", errorCount);
        putStatus("成功次数", successCount);
    }

    @Override
    public boolean isNeedScenario() {
        return true;
    }
}
