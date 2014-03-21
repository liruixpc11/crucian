package crucian.benchmark.tests;

import crucian.algorithms.coarsening.BasicCoarsening;
import crucian.benchmark.MappingAlgorithm;
import crucian.benchmark.algorithms.IsomorphismAlgorithm;
import crucian.benchmark.generators.RandomGtItmSubstrateNetworkGenerator;
import crucian.benchmark.generators.RandomGtItmVirtualNetworkGenerator;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

/**
 * Created at 14-3-19 下午7:41.
 *
 * @author lirui
 */
public class CoarseningPerformanceTest {
    public static void main(String[] args) throws Exception {
        long coarsenTimeTotal = 0;
        long mapTimeTotal = 0;
        long uncoarsenTimeTotal = 0;
        long copyTime = 0;
        long metricBandTime = 0;
        long nodeScanTime = 0;
        long metricTime = 0;
        long sortTime = 0;
        long combineTime = 0;
        double coarsenRate = 0;

        final double nanoPerSecond = 1000 * 1000 * 1000;
        final double nanoPerMilli = 1000 * 1000;
        final int times = 10;

        for (int i = 0; i < times; i++) {
            BasicCoarsening coarsening = new BasicCoarsening();
            VirtualNetwork virtualNetwork = new RandomGtItmVirtualNetworkGenerator(2, 2, 0.1, 5, 10).create();
            SubstrateNetwork substrateNetwork = new RandomGtItmSubstrateNetworkGenerator(100, 100, 1, 80, 100).create();
            MappingAlgorithm mappingAlgorithm = new IsomorphismAlgorithm();

            long start = System.nanoTime();
            VirtualNetwork coarsened = coarsening.coarsen(virtualNetwork, substrateNetwork);
            long coarsenTime = System.nanoTime();
            mappingAlgorithm.map(substrateNetwork, coarsened);
            long mapTime = System.nanoTime();
            VirtualNetwork uncoarsened = coarsening.undoCoarsen(coarsened);
            long uncoarsenTime = System.nanoTime();

            coarsenTimeTotal += coarsenTime - start;
            mapTimeTotal += mapTime - coarsenTime;
            uncoarsenTimeTotal += uncoarsenTime - mapTime;

            copyTime += coarsening.getCopyTime() - coarsening.getStartTime();
            metricBandTime += coarsening.getMetricBandwidthTime() - coarsening.getCopyTime();
            nodeScanTime += coarsening.getSnodeScanTime() - coarsening.getMetricBandwidthTime();
            metricTime += coarsening.getMetricTime() - coarsening.getSnodeScanTime();
            sortTime += coarsening.getSortTime() - coarsening.getMetricTime();
            combineTime += coarsening.getCombineTime() - coarsening.getSortTime();

            coarsenRate += (double) coarsened.getVertexCount() / virtualNetwork.getVertexCount();
        }

        System.out.printf("coarsen rate: %f\n", coarsenRate / times);

        System.out.println(String.format("%fms %fms %fms",
                coarsenTimeTotal / (times * nanoPerMilli),
                mapTimeTotal / (times * nanoPerMilli),
                uncoarsenTimeTotal / (times * nanoPerMilli))
        );

        System.out.println(String.format("copy[%fms] metricBandwidth[%fms] snodeScan[%fms] metric[%fms] sort[%fms] combine[%fms]",
                copyTime / (times * nanoPerMilli),
                metricBandTime / (times * nanoPerMilli),
                nodeScanTime / (times * nanoPerMilli),
                metricTime / (times * nanoPerMilli),
                sortTime / (times * nanoPerMilli),
                combineTime / (times * nanoPerMilli)
        ));
    }
}
