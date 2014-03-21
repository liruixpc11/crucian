package crucian.benchmark.tests;

import vnreal.demands.AbstractDemand;
import vnreal.demands.CpuDemand;
import vnreal.network.virtual.VirtualNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created at 14-3-19 下午7:29.
 *
 * @author lirui
 */
public class PerformanceTest {
    public static void main(String[] args) {
        Map<String, List<AbstractDemand>> abstractDemandMap = new HashMap<>();
        VirtualNode virtualNode = new VirtualNode(0);
        List<AbstractDemand> abstractDemands = new ArrayList<>();
        CpuDemand cpuDemand = new CpuDemand(virtualNode);
        virtualNode.add(cpuDemand);
        abstractDemands.add(cpuDemand);
        abstractDemandMap.put(CpuDemand.class.getName(), abstractDemands);

        long start = System.nanoTime();
        for (int i = 0; i < 1000 * 1000; i++) {
            CpuDemand cpuDemands = virtualNode.getSingle(CpuDemand.class);
        }
        System.out.println(System.nanoTime() - start);

        start = System.nanoTime();
        for (int i = 0; i < 1000 * 1000; i++) {
            List<AbstractDemand> cpuDemands = abstractDemandMap.get(CpuDemand.class.getName());
        }
        System.out.println(System.nanoTime() - start);
    }
}
