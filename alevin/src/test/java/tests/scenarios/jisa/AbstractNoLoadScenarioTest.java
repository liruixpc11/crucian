/* ***** BEGIN LICENSE BLOCK *****
 * Copyright (C) 2010-2011, The VNREAL Project Team.
 * 
 * This work has been funded by the European FP7
 * Network of Excellence "Euro-NF" (grant agreement no. 216366)
 * through the Specific Joint Developments and Experiments Project
 * "Virtual Network Resource Embedding Algorithms" (VNREAL). 
 *
 * The VNREAL Project Team consists of members from:
 * - University of Wuerzburg, Germany
 * - Universitat Politecnica de Catalunya, Spain
 * - University of Passau, Germany
 * See the file AUTHORS for details and contact information.
 * 
 * This file is part of ALEVIN (ALgorithms for Embedding VIrtual Networks).
 *
 * ALEVIN is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License Version 3 or later
 * (the "GPL"), or the GNU Lesser General Public License Version 3 or later
 * (the "LGPL") as published by the Free Software Foundation.
 *
 * ALEVIN is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * or the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License and
 * GNU Lesser General Public License along with ALEVIN; see the file
 * COPYING. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */
package tests.scenarios.jisa;

import mulavito.graph.generators.WaxmanGraphGenerator;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import vnreal.ToolKit;
import vnreal.demands.BandwidthDemand;
import vnreal.demands.CpuDemand;
import vnreal.network.Network;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.BandwidthResource;
import vnreal.resources.CpuResource;
import vnreal.resources.MultiCoreEnergyResource;
import vnreal.resources.StaticEnergyResource;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * @author Michael Duelli
 * @author Daniel Schlosser
 * @author Vlad Singeorzan
 * @author Michael Till Beck
 */
@Ignore
@RunWith(Parameterized.class)
public abstract class AbstractNoLoadScenarioTest {
    /**
     * A flag which controls automated parallelization based on file checking
     */
    protected boolean allowParallelRuns = false;

    private final static int numScenarios = 10;

    /**
     * # of runs with different random seeds if algorithm is non-deterministic
     */
    protected int numRunsPerScenario = 1;

    private static final int[] numSNodesArray = {100};

    private static final int[] numVNetsArray = {5};

    protected static final int[] numVNodesPerVNetArray = {5, 10, 15, 30, 50};

    private static final double[] alphaArray = {0.5};
    private static final double[] betaArray = {0.5};

    public static final int myminConsumption = 100;
    public static final int mymaxConsumption = 500;

    public static final int myminCPUResource = 1;
    public static final int mymaxCPUResource = 100;

    public static final int myminCPUDemand = 1;
    public static final int mymaxCPUDemand = 50;

    public static final int myminBandwidthResource = 1;
    public static final int mymaxBandwidthResource = 100;

    public static final int myminBandwidthDemand = 1;
    public static final int mymaxBandwidthDemand = 50;

    protected static class ScenarioData {
        public final int numSNodes;
        public final int numVNets;
        public final int numVNodesPerVNet;
        public final double alpha;
        public final double beta;

        ScenarioData(int numSNodes, int numVNets,
                     int numVNodesPerVNet,
                     double alpha, double beta) {
            this.numSNodes = numSNodes;
            this.numVNets = numVNets;
            this.numVNodesPerVNet = numVNodesPerVNet;
            this.alpha = alpha;
            this.beta = beta;
        }
    }

    @Parameters
    public static Collection<ScenarioData[]> data() {
        List<ScenarioData[]> data = new LinkedList<ScenarioData[]>();

        // Generate scenarios
        for (double alpha : alphaArray)
            for (double beta : betaArray)
                for (int numVNodesPerVNet : numVNodesPerVNetArray)
                    for (int numVNets : numVNetsArray)
                        for (int numSNodes : numSNodesArray)

                            data.add(new ScenarioData[]{new ScenarioData(numSNodes,
                                    numVNets, numVNodesPerVNet,
                                    alpha, beta)});

        return data;
    }

    protected String scenario_suffix;

    @Test
    public void runScenario() {
        // Generate scenario
        // FIXME UniformStream.setSeed(0);
        for (int i = 0; i < numScenarios; i++) {
            // Create new empty network stack.
            ToolKit.getScenario().setNetworkStack(
                    new NetworkStack(new SubstrateNetwork(),
                            new LinkedList<VirtualNetwork>()));

            final String suffix = data.numSNodes + "_" + data.numVNets + "_"
                    + data.numVNodesPerVNet + "_" + i;

            generate(data.numSNodes, data.numVNets,
                    data.numVNodesPerVNet,
                    data.alpha, data.beta);

            generateAdditionalConstraints();

            // Override abstract method to run algorithm.
            for (int j = 0; j < numRunsPerScenario; j++) {
                scenario_suffix = suffix + "_" + j;
                System.out.println("Run " + scenario_suffix);

                // Reset previous mappings
                ToolKit.getScenario().getNetworkStack().clearMappings();

                long startTime = System.currentTimeMillis();
                runAlgorithm(); // abstract method
                long elapsedTime = System.currentTimeMillis() - startTime;

                evaluate(scenario_suffix, elapsedTime);

            }
        }
    }

    protected void evaluate(String scenario_suffix, long elapsedTime) {
        // default, does nothing
    }

    protected void generateAdditionalConstraints() {
        // default, does nothing
    }

    protected String getName() {
        return getClass().getSimpleName();
    }

    // N.B. Made abstract to be able to parallelize algorithms.
    protected abstract void runAlgorithm();

    protected final ScenarioData data;

    protected AbstractNoLoadScenarioTest(ScenarioData data) {
        this.data = data;
    }


    /**
     * A method to generate a scenario with the specified parameters
     *
     * @param rho              The load of the substrate. 0 <= rho <= 1
     * @param numSNodes        The number of substrate nodes.
     * @param numVNets         The number of virtual networks.
     * @param numVNodesPerVNet The number of nodes per virtual network.
     * @param maxCPUres        The maximum value for the CPU resources.
     * @param maxBWres         The maximum value for the BW resources.
     * @param alpha            Alpha parameter for the {@link WaxmanGraphGenerator}. alpha >
     *                         0
     * @param beta             Beta parameter for the {@link WaxmanGraphGenerator}. beta <= 1
     * @param suffix
     */
    private void generate(int numSNodes, int numVNets,
                          int numVNodesPerVNet,
                          double alpha, double beta) {

        WaxmanGraphGenerator<SubstrateNode, SubstrateLink> sGenerator = new WaxmanGraphGenerator<SubstrateNode, SubstrateLink>(
                alpha, beta, false);
        WaxmanGraphGenerator<VirtualNode, VirtualLink> vGenerator = new WaxmanGraphGenerator<VirtualNode, VirtualLink>(
                alpha, beta, false);

        SubstrateNetwork sNetwork = new SubstrateNetwork();
        for (int i = 0; i < numSNodes; ++i) {
            SubstrateNode sn = new SubstrateNode();
            sNetwork.addVertex(sn);
        }
        sGenerator.generate(sNetwork);

        List<VirtualNetwork> vNetworks = new LinkedList<VirtualNetwork>();
        int layer = 1;
        for (int i = 0; i < numVNets; ++i) {
            VirtualNetwork vNetwork = new VirtualNetwork(layer);
            for (int n = 0; n < numVNodesPerVNet; ++n) {
                VirtualNode vn = new VirtualNode(layer);

                vNetwork.addVertex(vn);
            }
            vGenerator.generate(vNetwork);
            vNetworks.add(vNetwork);
        }

        NetworkStack stack = new NetworkStack(sNetwork, vNetworks);


        generateCPUResourcesAndDemands(
                stack,
                myminCPUResource, mymaxCPUResource,
                myminCPUDemand, mymaxCPUDemand);
        generateBandwidthResourceAndDemands(
                stack,
                myminBandwidthResource, mymaxBandwidthResource,
                myminBandwidthDemand, mymaxBandwidthDemand);

        ToolKit.getScenario().setNetworkStack(stack);
    }

    public abstract void generateCPUResourcesAndDemands(NetworkStack stack,
                                                        int minResourceCPU, int maxResourceCPU,
                                                        int minDemandCPU, int maxDemandCPU);

    public static void generateRandomCPUResourcesAndDemands(NetworkStack stack,
                                                            int minResourceCPU, int maxResourceCPU,
                                                            int minDemandCPU, int maxDemandCPU) {
        Random random = new Random();

        for (SubstrateNode n : stack.getSubstrate().getVertices()) {
            CpuResource cpu = new CpuResource(n);
            int value = (int) (minResourceCPU + (maxResourceCPU - minResourceCPU + 1) * random.nextDouble());
            cpu.setCycles((double) value);
            n.add(cpu);
        }

        boolean substrate = true;
        for (Network<?, ?, ?> aNetwork : stack) {
            if (substrate) {
                substrate = false;
                continue;
            }

            VirtualNetwork vNetwork = (VirtualNetwork) aNetwork;
            for (VirtualNode n : vNetwork.getVertices()) {
                CpuDemand cpu = new CpuDemand(n);
                int value = (int) (minDemandCPU + (maxDemandCPU - minDemandCPU + 1) * random.nextDouble());
                cpu.setDemandedCycles((double) value);
                n.add(cpu);
            }
        }
    }

    public abstract void generateBandwidthResourceAndDemands(
            NetworkStack stack,
            int minResourceBandwidth, int maxResourceBandwidth,
            int minDemandBandwidth, int maxDemandBandwidth);

    public static void generateRandomBandwidthResourceAndDemands(
            NetworkStack stack,
            int minResourceBandwidth, int maxResourceBandwidth,
            int minDemandBandwidth, int maxDemandBandwidth) {
        Random random = new Random();

        for (SubstrateLink l : stack.getSubstrate().getEdges()) {
            BandwidthResource bw = new BandwidthResource(l);
            int value = (int) (minResourceBandwidth + (maxResourceBandwidth - minResourceBandwidth + 1) * random.nextDouble());
            bw.setBandwidth((double) value);
            l.add(bw);
        }

        boolean substrate = true;
        for (Network<?, ?, ?> aNetwork : stack) {
            if (substrate) {
                substrate = false;
                continue;
            }

            VirtualNetwork vNetwork = (VirtualNetwork) aNetwork;
            for (VirtualLink l : vNetwork.getEdges()) {
                BandwidthDemand bw = new BandwidthDemand(l);
                int value = (int) (minDemandBandwidth + (maxDemandBandwidth - minDemandBandwidth + 1) * random.nextDouble());
                bw.setDemandedBandwidth((double) value);
                l.add(bw);
            }
        }
    }

    public static void generateFixedStaticEnergyConsumptionResources(
            NetworkStack stack, int consumption) {
        for (SubstrateNode n : stack.getSubstrate().getVertices()) {
            StaticEnergyResource r = new StaticEnergyResource(
                    n, consumption);

            n.add(r);
        }
    }

    public static void generateRandomStaticEnergyConsumptionResources() {
        NetworkStack stack = ToolKit.getScenario().getNetworkStack();
        Random random = new Random();

        for (SubstrateNode n : stack.getSubstrate().getVertices()) {
            int value = (int) (myminConsumption + (mymaxConsumption - myminConsumption + 1) * random.nextDouble());
            StaticEnergyResource r = new StaticEnergyResource(n, value);

            n.add(r);
        }
    }

    public static void generateMultiCoreEnergyConsumptionResources(
            NetworkStack stack, int idleConsumption,
            int additionalConsumptionPerCore, int numberOfCores) {

        for (SubstrateNode n : stack.getSubstrate().getVertices()) {
            MultiCoreEnergyResource r = new MultiCoreEnergyResource(n,
                    idleConsumption,
                    additionalConsumptionPerCore, numberOfCores);

            n.add(r);
        }
    }

}
