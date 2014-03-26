package crucian.benchmark.simulator;

import sun.net.www.content.text.plain;
import vnreal.network.substrate.SubstrateNetwork;

import java.util.List;

/**
 * Created at 14-3-24 下午9:29.
 *
 * @author lirui
 */
public class SimulatorFactory {
    public static Simulator create(String name,
                                   SimulatorListener simulatorListener,
                                   SubstrateNetwork substrateNetwork,
                                   SimulatorPlugin ... plugins) {
        Simulator simulator = new Simulator(name, simulatorListener, substrateNetwork);
        simulator.addPlugins(plugins);
        return simulator;
    }

    public static Simulator create(String name,
                                   SimulatorListener simulatorListener,
                                   SubstrateNetwork substrateNetwork,
                                   List<SimulatorPlugin> plugins) {
        return create(name, simulatorListener, substrateNetwork, plugins.toArray(new SimulatorPlugin[plugins.size()]));
    }
}
