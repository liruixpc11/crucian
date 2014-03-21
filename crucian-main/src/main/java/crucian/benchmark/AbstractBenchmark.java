package crucian.benchmark;

import crucian.benchmark.metrics.MetricResult;
import mulavito.algorithms.AbstractAlgorithmStatus;
import vnreal.network.NetworkStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LiRuiNet
 *         14-2-28 下午2:29
 */
public abstract class AbstractBenchmark implements Benchmark, IStatsProvider {
    private final Map<String, AbstractAlgorithmStatus> statusMap = new HashMap<String, AbstractAlgorithmStatus>();
    private NetworkStack networks = null;
    private String name;

    protected AbstractBenchmark() {
        BenchmarkAction action = getClass().getAnnotation(BenchmarkAction.class);
        if (action != null) {
            name = action.value();
        }
    }

    protected AbstractBenchmark(String name) {
        this.name = name;
    }

    @Override
    public List<AbstractAlgorithmStatus> getStats() {
        return new ArrayList<AbstractAlgorithmStatus>(statusMap.values());
    }

    @Override
    public AbstractAlgorithmStatus getStatus(String key) {
        return statusMap.get(key);
    }

    protected void putStatus(Map<String, MetricResult> metricResultMap) {
        for (MetricResult metricResult : metricResultMap.values()) {
            putStatus(metricResult);
        }
    }

    protected void putStatus(MetricResult metricResult) {
        statusMap.put(metricResult.getKey(), new MetricStatsAdapter(metricResult));
    }

    protected void putStatus(String key, String name, Number value) {
        statusMap.put(key, new BasicStatus(name, key, value));
    }

    protected void putStatus(String key, String name, Number value, Number maxValue) {
        statusMap.put(key, new BasicStatus(name, key, value, maxValue));
    }

    protected void putStatus(String name, Number value, Number maxValue) {
        statusMap.put(name, new BasicStatus(name, value, maxValue));
    }

    protected void putStatus(String name, Number value) {
        statusMap.put(name, new BasicStatus(name, value));
    }

    protected void putStatus(AbstractAlgorithmStatus status) {
        statusMap.put(status.getLabel(), status);
    }

    protected void clearStatus() {
        statusMap.clear();
    }

    protected NetworkStack getScenario() {
        return networks;
    }

    /**
     * 清理所有的映射关系
     */
    protected void clearScenario() {
        this.networks.clearMappings();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Benchmark setScenario(NetworkStack networks) {
        this.networks = networks;
        return this;
    }

    @Override
    public List<AbstractAlgorithmStatus> getStatus() {
        return new ArrayList<AbstractAlgorithmStatus>(statusMap.values());
    }

    @Override
    public Benchmark reportStatus(StatusReporter reporter) {
        reporter.report(getStatus());
        return this;
    }

    @Override
    public Benchmark clear() {
        clearStatus();
        clearScenario();
        return this;
    }

    @Override
    final public Benchmark evaluate(MappingAlgorithm mappingAlgorithm) {
        doEvaluate(mappingAlgorithm);
        return this;
    }

    protected abstract void doEvaluate(MappingAlgorithm mappingAlgorithm);
}
