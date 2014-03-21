package crucian.benchmark;

import crucian.benchmark.metrics.MetricResult;
import mulavito.algorithms.AbstractAlgorithmStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created at 14-3-19 上午9:03.
 *
 * @author lirui
 */
public class MetricStatsProvider implements IStatsProvider {
    private Map<String, MetricStatsAdapter> metricResultMap = new HashMap<>();

    public MetricStatsProvider(Map<String, MetricResult> metricResultMap) {
        for (MetricResult metricResult : metricResultMap.values()) {
            this.metricResultMap.put(metricResult.getKey(), new MetricStatsAdapter(metricResult));
        }
    }

    @Override
    public List<AbstractAlgorithmStatus> getStats() {
        return new ArrayList<AbstractAlgorithmStatus>(metricResultMap.values());
    }

    @Override
    public AbstractAlgorithmStatus getStatus(String key) {
        return metricResultMap.get(key);
    }
}
