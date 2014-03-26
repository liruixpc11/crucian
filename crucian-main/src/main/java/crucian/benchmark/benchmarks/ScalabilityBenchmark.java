package crucian.benchmark.benchmarks;

import crucian.benchmark.*;
import crucian.benchmark.generators.NetworkGenerator;
import crucian.benchmark.generators.RandomGtItmSubstrateNetworkGenerator;
import vnreal.ToolKit;
import vnreal.network.substrate.SubstrateNetwork;

/**
 * Created at 14-3-26 上午8:34.
 *
 * @author lirui
 */
@BenchmarkAction("可伸缩性测试")
public class ScalabilityBenchmark extends AbstractBenchmark implements Runnable {
    private boolean useSubstrateInStack;
    private int nodeCountInterval;
    private int timesPerTest;
    private int minNodeCount;
    private int maxNodeCount;
    private double connectProbability; // between [0, 1)
    private double minResourceDemand;
    private double maxResourceDemand;
    private int substrateNodeCount;
    private int substrateEdgeCount;
    private double minSResource;
    private double maxSResource;

    private NetworkGenerator<SubstrateNetwork> substrateNetworkGenerator;
    private SubstrateNetwork substrateNetwork;

    @BenchmarkFactory
    public static ScalabilityBenchmark create(
            @BenchmarkParam("节点数区间") @DefaultValue("5") int nodeCountInterval,
            @BenchmarkParam("单次测试轮数") @DefaultValue("100") int timesPerTest,
            @BenchmarkParam("VNR最小节点数量") @DefaultValue("2") int minNodeCount,
            @BenchmarkParam("VNR最大节点数量") @DefaultValue("20") int maxNodeCount,
            @BenchmarkParam("VNR节点连接概率") @DefaultValue("0.2") double connectProbability,
            @BenchmarkParam("VNR最小资源需求") @DefaultValue("1") double minResourceDemand,
            @BenchmarkParam("VNR最大资源需求") @DefaultValue("10") double maxResourceDemand,
            @BenchmarkParam("SN节点数量") @DefaultValue("100") int substrateNodeCount,
            @BenchmarkParam("SN边数量") @DefaultValue("500") int substrateEdgeCount,
            @BenchmarkParam("SN最小资源") @DefaultValue("80") double minSResource,
            @BenchmarkParam("SN最大资源") @DefaultValue("100") double maxSResource,
            @BenchmarkParam("使用底层网络") @DefaultValue("false") boolean useSubstrateInStack
    ) {
        ScalabilityBenchmark benchmark = new ScalabilityBenchmark();
        benchmark.useSubstrateInStack = useSubstrateInStack;
        benchmark.minNodeCount = minNodeCount;
        benchmark.maxNodeCount = maxNodeCount;
        benchmark.nodeCountInterval = nodeCountInterval;
        benchmark.timesPerTest = timesPerTest;
        benchmark.minNodeCount = minNodeCount;
        benchmark.maxNodeCount = maxNodeCount;
        benchmark.connectProbability = connectProbability;
        benchmark.minResourceDemand = minResourceDemand;
        benchmark.maxResourceDemand = maxResourceDemand;
        benchmark.substrateNodeCount = substrateNodeCount;
        benchmark.substrateEdgeCount = substrateEdgeCount;
        benchmark.minSResource = minSResource;
        benchmark.maxSResource = maxSResource;
        benchmark.useSubstrateInStack = useSubstrateInStack;
        //new WaxmanVirtualNetworkGenerator(minNodeCount, maxNodeCount, 0.1, minResourceDemand, maxResourceDemand)
        /*new BasicVirtualNetworkGenerator(minNodeCount,
                maxNodeCount,
                connectProbability,
                minResourceDemand,
                maxResourceDemand)*/
        /*benchmark.substrateNetworkGenerator = new BriteFileSubstrateNetworkGenerator(minSResource,
                maxSResource,
                "/home/lirui/tmp/test.brite");*/
        benchmark.substrateNetworkGenerator = new RandomGtItmSubstrateNetworkGenerator(substrateNodeCount, substrateNodeCount,
                ((double) substrateEdgeCount) * 2 / (substrateNodeCount * (substrateNodeCount - 1)),
                minSResource, maxSResource);
        return benchmark;
    }

    @Override
    protected void doEvaluate(MappingAlgorithm mappingAlgorithm) {
        if (useSubstrateInStack) {
            substrateNetwork = ToolKit.getScenario().getNetworkStack().getSubstrate();
        } else {
            substrateNetwork = substrateNetworkGenerator.create();
        }

        new Thread(this).start();
    }

    @Override
    public boolean isNeedScenario() {
        return useSubstrateInStack;
    }

    @Override
    public void run() {
        for (int nodeCount = minNodeCount; nodeCount <= maxNodeCount; nodeCount += nodeCountInterval) {
        }
    }
}
