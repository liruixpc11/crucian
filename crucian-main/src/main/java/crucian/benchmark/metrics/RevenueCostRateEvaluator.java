package crucian.benchmark.metrics;

import crucian.benchmark.QualityMetrics;
import vnreal.network.virtual.VirtualNetwork;

import java.util.Map;

/**
 * Created at 14-3-18 下午10:05.
 *
 * @author lirui
 */
public class RevenueCostRateEvaluator implements MetricEvaluator<VirtualNetwork> {
    @Override
    public Map<String, MetricResult> evaluate(VirtualNetwork virtualNetwork) {
        return new BasicMetricResult(QualityMetrics.revenueCostRate, "收益开销比", cost(virtualNetwork)).toMap();
    }

    public static double cost(VirtualNetwork virtualNetwork) {
        return RevenueEvaluator.revenue(virtualNetwork) / VirtualCostEvaluator.cost(virtualNetwork);
    }
}
