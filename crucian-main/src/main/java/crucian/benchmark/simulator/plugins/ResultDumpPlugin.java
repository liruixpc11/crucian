package crucian.benchmark.simulator.plugins;

import crucian.benchmark.metrics.RevenueCostRateEvaluator;
import crucian.benchmark.metrics.RevenueEvaluator;
import crucian.benchmark.metrics.SubstrateCostEvaluator;
import crucian.benchmark.metrics.VirtualCostEvaluator;
import crucian.benchmark.simulator.Event;
import crucian.benchmark.simulator.Simulator;
import crucian.benchmark.simulator.SimulatorPluginAdapter;
import vnreal.network.substrate.SubstrateNetwork;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created at 14-3-24 下午9:15.
 *
 * @author lirui
 */
public class ResultDumpPlugin extends SimulatorPluginAdapter {
    private String filename;
    private PrintWriter printWriter;
    private SubstrateNetwork substrateNetwork;

    private long successCount;
    private long errorCount;

    public ResultDumpPlugin(String filename) {
        this.filename = filename;
    }

    @Override
    public void initialize(Simulator simulator) {
        substrateNetwork = simulator.getSubstrateNetwork();
        try {
            String idString = String.valueOf(simulator.getId());
            if (simulator.getName() != null && simulator.getName().length() > 0) {
                idString += "(" + simulator.getName() + ")";
            }

            Path d = Paths.get("simulationResult", idString);
            Path p = d.resolve(filename);
            Files.createDirectories(d);
            printWriter = new PrintWriter(p.toFile());
            printWriter.printf("%10s, %10s, %10s, %10s, %10s, %10s, %10s, %10s, %10s\n",
                    "arriveT", "type", "costT", "#success", "#error", "s#cost", "s#total", "v#revenue", "v#cost");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void uninitialize(Simulator simulator) {
        printWriter.close();
    }

    @Override
    public void onEventEnd(Simulator simulator, Event event) {
        if (event.getType() == Event.Type.Arrive) {
            if (event.isSuccess()) {
                successCount++;
            } else {
                errorCount++;
            }
        }

        SubstrateCostEvaluator.CostTotal costTotal = SubstrateCostEvaluator.costAll(substrateNetwork);
        double revenue = RevenueEvaluator.revenue(event.getVirtualNetwork());
        double virtualCost = VirtualCostEvaluator.cost(event.getVirtualNetwork());

        printWriter.printf("%10s, %10s, %10s, %10s, %10s, %10s, %10s, %10s, %10s\n",
                event.getTime(),
                event.getType() == Event.Type.Arrive ? "a" : "d",
                event.getHandleNanoseconds(),
                successCount,
                errorCount,
                costTotal.getCost(),
                costTotal.getTotal(),
                revenue,
                virtualCost);
    }
}
