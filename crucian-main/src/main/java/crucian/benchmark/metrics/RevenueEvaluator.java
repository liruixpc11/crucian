package crucian.benchmark.metrics;

import crucian.benchmark.QualityMetrics;
import vnreal.demands.BandwidthDemand;
import vnreal.demands.CpuDemand;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

import java.util.Map;

/**
 * Created at 14-3-18 下午9:53.
 *
 * @author lirui
 */
public class RevenueEvaluator implements MetricEvaluator<VirtualNetwork> {
    @Override
    public Map<String, MetricResult> evaluate(VirtualNetwork virtualNetwork) {
        return new BasicMetricResult(QualityMetrics.revenue, "收益", revenue(virtualNetwork)).toMap();
    }

    public static double revenue(VirtualNetwork virtualNetwork) {
        double nodeRevenue = 0;
        for (VirtualNode virtualNode : virtualNetwork.getVertices()) {
            for (CpuDemand cpuDemand : virtualNode.get(CpuDemand.class)) {
                nodeRevenue += cpuDemand.getDemandedCycles();
            }
        }

        double linkRevenue = 0;
        for (VirtualLink virtualLink : virtualNetwork.getEdges()) {
            for (BandwidthDemand bandwidthDemand : virtualLink.get(BandwidthDemand.class)) {
                linkRevenue += bandwidthDemand.getDemandedBandwidth();
            }
        }

        return nodeRevenue + linkRevenue;
    }
}
