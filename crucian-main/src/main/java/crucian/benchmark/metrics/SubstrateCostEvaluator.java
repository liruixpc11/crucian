package crucian.benchmark.metrics;

import crucian.benchmark.QualityMetrics;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.resources.BandwidthResource;
import vnreal.resources.CpuResource;

import java.util.Map;

/**
 * Created at 14-3-18 下午7:04.
 *
 * @author lirui
 */
public class SubstrateCostEvaluator implements MetricEvaluator<SubstrateNetwork> {
    public static class CostTotal {
        private double cost = 0;
        private double total = 0;

        public CostTotal(double cost, double total) {
            this.cost = cost;
            this.total = total;
        }

        public double getCost() {
            return cost;
        }

        public double getTotal() {
            return total;
        }

        @Override
        public String toString() {
            return "CostTotal{" +
                    "cost=" + cost +
                    ", total=" + total +
                    '}';
        }
    }

    @Override
    public Map<String, MetricResult> evaluate(SubstrateNetwork substrateNetwork) {
        CostTotal costTotal = costAll(substrateNetwork);
        return new BasicMetricResult(QualityMetrics.substrateCost, "底层网络开销", costTotal.cost, costTotal.total).toMap();
    }

    public static double cost(SubstrateNetwork substrateNetwork) {
        return costAll(substrateNetwork).cost;
    }

    public static CostTotal costAll(SubstrateNetwork substrateNetwork) {
        double linkCost = 0;
        double linkTotal = 0;
        for (SubstrateLink currSLink : substrateNetwork.getEdges()) {
            for (BandwidthResource bandwidthResource : currSLink.get(BandwidthResource.class)) {
                linkCost += bandwidthResource.getOccupiedBandwidth();
                linkTotal += bandwidthResource.getBandwidth();
            }
        }

        double nodeCost = 0;
        double nodeTotal = 0;
        for (SubstrateNode substrateNode : substrateNetwork.getVertices()) {
            for (CpuResource cpuResource : substrateNode.get(CpuResource.class)) {
                nodeCost += cpuResource.getOccupiedCycles();
                nodeTotal += cpuResource.getCycles();
            }
        }

        return new CostTotal(nodeCost + linkCost, nodeTotal + linkTotal);
    }
}
