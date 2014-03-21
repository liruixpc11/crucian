package crucian.benchmark;

import crucian.benchmark.metrics.MetricResult;
import mulavito.algorithms.AbstractAlgorithmStatus;

/**
 * Created at 14-3-19 上午9:04.
 *
 * @author lirui
 */
public class MetricStatsAdapter extends AbstractAlgorithmStatus {
    private MetricResult metricResult;

    public MetricStatsAdapter(MetricResult metricResult) {
        super(metricResult.getName());
        this.metricResult = metricResult;
    }

    @Override
    public Number getValue() {
        return metricResult.getValue();
    }

    @Override
    public Number getMaximum() {
        return metricResult.getMaxValue();
    }
}
