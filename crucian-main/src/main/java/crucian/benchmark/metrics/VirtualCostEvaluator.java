package crucian.benchmark.metrics;

import crucian.benchmark.QualityMetrics;
import vnreal.demands.BandwidthDemand;
import vnreal.demands.CpuDemand;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

import java.util.Map;

/**
 * Created at 14-3-18 下午9:59.
 *
 * @author lirui
 */
public class VirtualCostEvaluator implements MetricEvaluator<VirtualNetwork> {
    @Override
    public Map<String, MetricResult> evaluate(VirtualNetwork virtualNetwork) {
        return new BasicMetricResult(QualityMetrics.virtualCost, "底层网络开销", cost(virtualNetwork)).toMap();
    }

    public static double cost(VirtualNetwork virtualNetwork) {
        double nodeCost = 0;
        for (VirtualNode virtualNode : virtualNetwork.getVertices()) {
            for (CpuDemand cpuDemand : virtualNode.get(CpuDemand.class)) {
                nodeCost += cpuDemand.getDemandedCycles() * cpuDemand.getMappings().size();
            }
        }

        double linkCost = 0;
        for (VirtualLink virtualLink : virtualNetwork.getEdges()) {
            for (BandwidthDemand bandwidthDemand : virtualLink.get(BandwidthDemand.class)) {
                linkCost += bandwidthDemand.getDemandedBandwidth() * bandwidthDemand.getMappings().size();
            }
        }

        return nodeCost + linkCost;
    }
}
