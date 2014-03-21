package crucian.benchmark.benchmarks;

import crucian.benchmark.*;
import crucian.benchmark.generators.NetworkGenerator;
import crucian.benchmark.generators.RandomGtItmSubstrateNetworkGenerator;
import crucian.benchmark.generators.RandomGtItmVirtualNetworkGenerator;
import crucian.benchmark.metrics.*;
import crucian.benchmark.simulator.Event;
import crucian.benchmark.simulator.Simulator;
import crucian.benchmark.simulator.SimulatorListener;
import vnreal.ToolKit;
import vnreal.gui.GUI;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

import java.awt.*;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created at 14-3-3 下午1:15.
 *
 * @author lirui
 */
@BenchmarkAction("映射质量")
public class QualityBenchmark extends AbstractBenchmark implements ITimeStatsProvider {
    private static final double nanoPerSecond = 1000 * 1000 * 1000;

    private final RandomUtility randomUtility = new RandomUtility();
    private long currentSimulatorTime = 0;
    private int maxTime;
    private int avgEvery100;
    private int avgAge;
    private int ageSigma;
    private int minNodeCount;
    private int maxNodeCount;
    private double connectProbability; // between [0, 1)
    private double minResourceDemand;
    private double maxResourceDemand;
    private int substrateNodeCount;
    private int substrateEdgeCount;
    private double minSResource;
    private double maxSResource;
    private boolean useSubstrateInStack = false;

    // counters
    private int acceptCount = 0;
    private int rejectCount = 0;
    private AtomicInteger requestInQueue = new AtomicInteger(0);
    private long mapStartNanoseconds;
    private long totalAcceptNanoseconds;
    private long totalRejectNanoseconds;
    private double totalRevenue = 0;
    private double totalCost = 0;

    // policies
    private NetworkGenerator<VirtualNetwork> virtualNetworkGenerator;
    private NetworkGenerator<SubstrateNetwork> substrateNetworkGenerator;
    private SubstrateNetwork substrateNetwork;
    private VirtualNetwork firstVirtualNetwork;

    private long currentTime = 0;

    private StatusDisplayable[] displayableList = new StatusDisplayable[]{
            new StatsDialog(GUI.getInstance(), this, "当前统计信息"),
            new QualityChartDialog(GUI.getInstance(), "请求接受率", this)
    };

    private MetricEvaluator<VirtualNetwork> virtualMetricEvaluator = new CompositeEvaluator<>(
            VirtualCostEvaluator.class,
            RevenueEvaluator.class
    );

    private MetricEvaluator<SubstrateNetwork> substrateMetricEvaluator = new CompositeEvaluator<>(
            SubstrateCostEvaluator.class
    );

    @Override
    public boolean isNeedScenario() {
        return useSubstrateInStack;
    }

    /**
     * 当前模拟器时间，不完全准确
     *
     * @return 当前模拟器时间
     */
    @Override
    public long getTime() {
        return currentSimulatorTime;
    }

    private class Listener implements SimulatorListener {
        private boolean first = true;

        @Override
        public boolean onEmpty(Simulator simulator) {
            if (first) {
                System.out.println("初始化");
                first = false;
                loadEvents(simulator);
                return true;
            }

            updateStats(true);
            ToolKit.getScenario().setNetworkStack(new NetworkStack(substrateNetwork, Collections.singletonList(firstVirtualNetwork)));
            System.out.println("结束");
            return false;
        }

        private void loadEvents(Simulator simulator) {
            long virtualCount = 0;
            for (int i = 0; i < maxTime / 100; i++) {
                long count = randomUtility.nextPoison(avgEvery100);
                virtualCount += count;
                System.out.printf("[%d] 生成%d个网络拓扑\n", i + 1, count);
                for (int j = 0; j < count; j++) {
                    System.out.printf("[%d] 生成第%d(%d)个网络拓扑\n", i + 1, j + 1, count);
                    VirtualNetwork virtualNetwork = createVirtualNetwork();
                    if (firstVirtualNetwork == null) {
                        firstVirtualNetwork = virtualNetwork;
                    }

                    simulator.getEventQueue().enqueue(new Event(Event.Type.Arrive,
                            i * 100 + randomUtility.nextInt(100),
                            virtualNetwork));
                }
            }
        }

        private VirtualNetwork createVirtualNetwork() {
            return virtualNetworkGenerator.create();
        }

        @Override
        public void onSuccess(Simulator simulator, Event event) {
            if (event.getType().equals(Event.Type.Arrive)) {
                totalAcceptNanoseconds += System.nanoTime() - mapStartNanoseconds;
                currentSimulatorTime = event.getTime();
                simulator.getEventQueue().enqueue(new Event(Event.Type.Depart,
                        event.getTime() + randomUtility.nextInt(2 * ageSigma + 1) + avgAge - ageSigma,
                        event.getVirtualNetwork()));
                requestInQueue.incrementAndGet();
                acceptCount++;
                totalRevenue += RevenueEvaluator.revenue(event.getVirtualNetwork());
                totalCost += VirtualCostEvaluator.cost(event.getVirtualNetwork());
                updateStats();
            } else if (event.getType().equals(Event.Type.Depart)) {
                requestInQueue.decrementAndGet();
            }
        }

        @Override
        public void onError(Simulator simulator, Event event, Throwable cause) {
            if (event.getType().equals(Event.Type.Arrive)) {
                totalRejectNanoseconds += System.nanoTime() - mapStartNanoseconds;
                currentSimulatorTime = event.getTime();
                rejectCount++;
                updateStats();
                cause.printStackTrace();
            }
        }

        @Override
        public void preMap(Simulator simulator, Event event) {
            currentSimulatorTime = event.getTime();
            mapStartNanoseconds = System.nanoTime();
        }

        private void updateStats() {
            updateStats(false);
        }

        private void updateStats(boolean always) {
            long tempTime = System.currentTimeMillis() / 1000;
            if (always || tempTime != currentTime) {
                System.out.println("更新统计资料" + currentTime);
                final Map<String, MetricResult> metricResultMap = substrateMetricEvaluator.evaluate(substrateNetwork);
                currentTime = tempTime;
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        putStatus("总映射时间（s）", (double) (totalAcceptNanoseconds + totalRejectNanoseconds) / nanoPerSecond);
                        putStatus("成功次数", acceptCount, acceptCount + rejectCount);
                        putStatus("成功总时间（s）", (double) totalAcceptNanoseconds / nanoPerSecond);
                        putStatus("失败总时间（s）", (double) totalRejectNanoseconds / nanoPerSecond);
                        putStatus(QualityMetrics.avgTime, "平均映射时间（s/个）", (double) (totalAcceptNanoseconds + totalRejectNanoseconds) / ((acceptCount + rejectCount) * nanoPerSecond));
                        putStatus("生存请求数量", requestInQueue.get(), requestInQueue.get());
                        putStatus(QualityMetrics.acceptRate, "接受率", (acceptCount * 100.0) / (acceptCount + rejectCount));
                        putStatus(QualityMetrics.revenue, "累计收益", totalRevenue, totalCost);
                        putStatus(metricResultMap);
                        for (StatusDisplayable statusDisplayable : displayableList) {
                            try {
                                statusDisplayable.updateStats();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
            }
        }
    }

    @BenchmarkFactory
    public static QualityBenchmark create(@BenchmarkParam("最大运行时间") @DefaultValue("5000") int maxTime,
                                          @BenchmarkParam("每一百时间单位内请求平均数") @DefaultValue("5") int avgEvery100,
                                          @BenchmarkParam("VN平均寿命") @DefaultValue("500") int avgAge,
                                          @BenchmarkParam("VN寿命最大差值") @DefaultValue("50") int ageSigma,
                                          @BenchmarkParam("VNR最小节点数量") @DefaultValue("2") int minNodeCount,
                                          @BenchmarkParam("VNR最大节点数量") @DefaultValue("20") int maxNodeCount,
                                          @BenchmarkParam("VNR节点连接概率") @DefaultValue("0.2") double connectProbability,
                                          @BenchmarkParam("VNR最小资源需求") @DefaultValue("1") double minResourceDemand,
                                          @BenchmarkParam("VNR最大资源需求") @DefaultValue("10") double maxResourceDemand,
                                          @BenchmarkParam("SN节点数量") @DefaultValue("100") int substrateNodeCount,
                                          @BenchmarkParam("SN边数量") @DefaultValue("500") int substrateEdgeCount,
                                          @BenchmarkParam("SN最小资源") @DefaultValue("80") double minSResource,
                                          @BenchmarkParam("SN最大资源") @DefaultValue("100") double maxSResource,
                                          @BenchmarkParam("是否使用网络栈中的底层网络") @DefaultValue("false") boolean useSubstrateInStack
    ) {
        QualityBenchmark benchmark = new QualityBenchmark();
        benchmark.maxTime = maxTime;
        benchmark.avgEvery100 = avgEvery100;
        benchmark.avgAge = avgAge;
        benchmark.ageSigma = ageSigma;
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
        benchmark.virtualNetworkGenerator =
                new RandomGtItmVirtualNetworkGenerator(minNodeCount, maxNodeCount, connectProbability, minResourceDemand, maxResourceDemand);
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
    public void doEvaluate(MappingAlgorithm mappingAlgorithm) {
        // 设置初始接受率
        putStatus(QualityMetrics.acceptRate, "接受率", 100);

        acceptCount = rejectCount = 0;
        if (useSubstrateInStack) {
            substrateNetwork = getScenario().getSubstrate();
        } else {
            substrateNetwork = substrateNetworkGenerator.create();
        }

        System.out.println("底层节点数: " + substrateNetwork.getVertexCount() + "\n底层连接数: " + substrateNetwork.getEdgeCount());
        Simulator simulator = new Simulator(new Listener(), substrateNetwork);
        simulator.setMappingAlgorithm(mappingAlgorithm);
        for (StatusDisplayable statusDisplayable : displayableList) {
            statusDisplayable.setAlgorithmName(mappingAlgorithm.getClass().getSimpleName());
            statusDisplayable.reset();
            statusDisplayable.updateStats();
            statusDisplayable.setVisible(true);
        }
        new Thread(simulator).start();
    }
}
