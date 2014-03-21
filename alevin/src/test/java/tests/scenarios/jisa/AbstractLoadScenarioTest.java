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
import vnreal.demands.AbstractDemand;
import vnreal.demands.BandwidthDemand;
import vnreal.demands.CpuDemand;
import vnreal.evaluations.metrics.*;
import vnreal.gui.dialog.ConstraintsGeneratorDialog;
import vnreal.gui.dialog.ScenarioWizard;
import vnreal.io.ScenarioExporter;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.BandwidthResource;
import vnreal.resources.CpuResource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author Michael Duelli
 * @author Daniel Schlosser
 * @author Vlad Singeorzan
 */
@Ignore
@RunWith(Parameterized.class)
public abstract class AbstractLoadScenarioTest {
    /**
     * A flag which controls automated parallelization based on file checking
     */
    protected boolean allowParallelRuns = false;

    private final static int numScenarios = 5;

    /**
     * # of runs with different random seeds if algorithm is non-deterministic
     */
    protected int numRunsPerScenario = 10;

    private static final int[] numSNodesArray = {50};

    private static final int[] numVNetsArray = {10, 15, 20};

    protected static final int[] numVNodesPerVNetArray = {20};

    private static final double[] rhoArray = {0.2, 0.3, 0.4, 0.5, 0.6, 0.7};

    private static final double[] maxCPUresArray = {100.0};
    private static final double[] maxBWresArray = {100.0};

    private static final double[] alphaArray = {0.5};
    private static final double[] betaArray = {0.5};

    protected static class ScenarioData {
        public final double rho;
        public final int numSNodes;
        public final int numVNets;
        public final int numVNodesPerVNet;
        public final double maxCPUres;
        public final double maxBWres;
        public final double alpha;
        public final double beta;

        ScenarioData(double rho, int numSNodes, int numVNets,
                     int numVNodesPerVNet, double maxCPUres, double maxBWres,
                     double alpha, double beta) {
            this.rho = rho;
            this.numSNodes = numSNodes;
            this.numVNets = numVNets;
            this.numVNodesPerVNet = numVNodesPerVNet;
            this.maxCPUres = maxCPUres;
            this.maxBWres = maxBWres;
            this.alpha = alpha;
            this.beta = beta;
        }
    }

    @Parameters
    public static Collection<ScenarioData[]> data() {
        List<ScenarioData[]> data = new LinkedList<ScenarioData[]>();

        // Generate scenarios
        for (double beta : betaArray)
            for (double alpha : alphaArray)
                for (double maxBWres : maxBWresArray)
                    for (double maxCPUres : maxCPUresArray)
                        for (int numVNodesPerVNet : numVNodesPerVNetArray)
                            for (int numVNets : numVNetsArray)
                                for (int numSNodes : numSNodesArray)
                                    for (double rho : rhoArray) {
                                        data.add(new ScenarioData[]{new ScenarioData(rho, numSNodes,
                                                numVNets, numVNodesPerVNet,
                                                maxCPUres, maxBWres, alpha,
                                                beta)});
                                    }

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
                    + data.numVNodesPerVNet + "_" + i + "_" + data.rho;

            String basePath = System.getProperty("user.home") + File.separator
                    + getName();
            if (!new File(basePath).exists()) {
                new File(basePath).mkdir();
            }

            final String template_output = basePath + File.separator
                    + "Scenario-template_" + suffix + ".xml";

            // Parallelization:
            // Check if another instance of this JUnit already has created the
            // template. If so, we can skip the template creation.
            if (!allowParallelRuns || !(new File(template_output)).exists()) {
                // FIXME UniformStream.setSeed(i);
                generate(data.rho, data.numSNodes, data.numVNets,
                        data.numVNodesPerVNet, data.maxCPUres, data.maxBWres,
                        data.alpha, data.beta, suffix, basePath);

                generateAdditionalConstraints();

                try {
                    // Name schema:
                    // Scenario-template_[size of substrate network]_[number of
                    // virtual networks]_[number of nodes per network]_[number
                    // for generation]_[mean load].xml
                    ScenarioExporter exporter = new ScenarioExporter(
                            template_output);
                    exporter.write();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else
                System.out.println("Skip template gen. " + template_output);

            // Override abstract method to run algorithm.
            for (int j = 0; j < numRunsPerScenario; j++) {
                scenario_suffix = suffix + "_" + j;
                System.out.println("Run " + scenario_suffix);

                final String scenario_output = basePath + File.separator
                        + "Scenario-mapped_" + scenario_suffix + ".xml";

                // Parallelization:
                // Check if another instance of this JUnit might have created
                // this output file already. If so, just continue with the
                // next test run.
                if (allowParallelRuns && new File(scenario_output).exists()) {
                    System.out.println("Skip mapping of " + scenario_output);
                    continue;
                }

                // Reset previous mappings
                ToolKit.getScenario().getNetworkStack().clearMappings();

                long startTime = System.currentTimeMillis();
                runAlgorithm(); // abstract method
                long elapsedTime = System.currentTimeMillis() - startTime;

                evaluate(scenario_suffix, elapsedTime);

                try {
                    // Name schema
                    // Scenario-mapped_[size of substrate network]_[number of
                    // virtual networks]_[number of nodes per network]_[number
                    // for generation]_[mean load].xml
                    ScenarioExporter exporter = new ScenarioExporter(
                            scenario_output);
                    exporter.write();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
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

    protected AbstractLoadScenarioTest(ScenarioData data) {
        this.data = data;
    }

    public static List<EvaluationMetric> getDefaultMetrics() {
        List<EvaluationMetric> result = new LinkedList<EvaluationMetric>();

        result.add(new AcceptedVnrRatio());
        result.add(new AvActiveLinkStress());
        result.add(new AvActiveNodeStress());
        result.add(new AvAllPathLength());
        result.add(new AvLinkStress());
        result.add(new AvNodeStress());
        result.add(new AvPathLength());
        result.add(new Cost());
        result.add(new CostRevenue(false));
        result.add(new CostRevTimesMappedRev(false));
        result.add(new EnergyConsumption());
        result.add(new LinkCostPerVnr());
        result.add(new LinkUtilization());
        result.add(new MappedRevenue(false));
        result.add(new MaxLinkStress());
        result.add(new MaxNodeStress());
        result.add(new MaxPathLength());
        result.add(new NodeUtilization());
        result.add(new RatioMappedRevenue(false));
        result.add(new RejectedNetworksNumber());
        result.add(new RemainingLinkResource());
        result.add(new RevenueCost(false));
        result.add(new RunningNodes());
//		result.add(new RunningTime(time));
        result.add(new SolelyForwardingHops());
        result.add(new TotalRevenue(false));

        return result;
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
    private void generate(double rho, int numSNodes, int numVNets,
                          int numVNodesPerVNet, double maxCPUres, double maxBWres,
                          double alpha, double beta, String suffix, String basePath) {
        if (alpha <= 0)
            throw new AssertionError("Alpha not > 0.");
        if (beta > 1)
            throw new AssertionError("Beta not <= 1");
        if (rho < 0 || rho > 1)
            throw new AssertionError("Not 0 <= rho <= 1.");
        if (numSNodes < numVNodesPerVNet)
            throw new AssertionError("More virtual than substrateNodes in VN");

        // Works if rho * kmax = 1 holds
        if (!(Math.pow(numSNodes / numVNodesPerVNet, 2) <= numVNets))
            throw new AssertionError("n^2 > k_max");

        // generate the topology first

        // this arrays are needed, because the generate method in ScenarioWizard
        // is more general and accepts distinct values for the number of nodes,
        // alpha and beta for each virtual network.
        int[] numVNodes = new int[numVNets];
        Arrays.fill(numVNodes, numVNodesPerVNet);
        double[] vnAlpha = new double[numVNets];
        Arrays.fill(vnAlpha, alpha);
        double[] vnBeta = new double[numVNets];
        Arrays.fill(vnBeta, beta);

        ScenarioWizard.generateTopology(numSNodes, alpha, beta, numVNets,
                numVNodes, vnAlpha, vnBeta);

        // compute demand parameters so that we have the desired substrate load
        // store them in an array, so it can easily be used in the next step
        double meanCPUres = 0.5 * maxCPUres; // uniform
        double[] maxCPUdem = new double[numVNets];
        for (int i = 0; i < maxCPUdem.length; i++) {
            double meanCPUdem = rho
                    * ((meanCPUres * numSNodes) / (numVNodesPerVNet * numVNets));
            maxCPUdem[i] = 2 * meanCPUdem; // uniform in [0, maxCPUdem]
        }

        // -------------------
        // the actual creation substrate

        // create the needed lists for the constraints generation as specified
        // by the generateConstraints method in ConstraintsGeneratorDialog
        // resources
        List<Class<?>> resClasses = new ArrayList<Class<?>>();
        List<String[]> resParamNames = new ArrayList<String[]>();
        List<String[]> resParamMaxValues = new ArrayList<String[]>();

        // for CPU resource
        resClasses.add(CpuResource.class);
        resParamNames.add(new String[]{"cycles"});
        resParamMaxValues.add(new String[]{Double.toString(maxCPUres)});
        // for BW resource
        resClasses.add(BandwidthResource.class);
        resParamNames.add(new String[]{"bandwidth"});
        resParamMaxValues.add(new String[]{Double.toString(maxBWres)});

        // finally, generate the constraints using the data structures created
        // ConstraintsGeneratorDialog.generateConstraints(resClasses,
        // resParamNames, resParamMaxValues, demClasses, demParamNames,
        // demParamMaxValues);
        ConstraintsGeneratorDialog.generateConstraintsSubstrate(resClasses,
                resParamNames, resParamMaxValues);

        SubstrateNetwork substrate = ToolKit.getScenario().getNetworkStack()
                .getSubstrate();
        double maxCPUResources = 0.0;
        for (SubstrateNode n : substrate.getVertices())
            for (AbstractResource res : n.get())
                if (res instanceof CpuResource)
                    maxCPUResources = Math.max(maxCPUResources,
                            ((CpuResource) res).getCycles());

        double maxBWResources = 0.0;
        for (SubstrateLink l : substrate.getEdges())
            for (AbstractResource res : l.get())
                if (res instanceof BandwidthResource)
                    maxBWResources = Math.max(maxBWResources,
                            ((BandwidthResource) res).getBandwidth());

        // Waxman probability on 1x1 square area with uniform coordinate
        // distribution.
        final double L = Math.sqrt(2);
        double meanD = 0.5; // FIXME proof
        double meanP = alpha * Math.exp(-meanD / (beta * L));

        double meanSNetEdges = meanP * numSNodes * (numSNodes - 1);
        double meanVNetEdges = meanP * numVNodesPerVNet
                * (numVNodesPerVNet - 1);

        double meanBWres = 0.5 * maxBWResources; // uniform
        double[] maxBWdem = new double[numVNets];
        for (int i = 0; i < numVNets; i++) {
            double meanBWdem = rho
                    * ((meanBWres * meanSNetEdges) / (meanVNetEdges * numVNets));
            maxBWdem[i] = 2 * meanBWdem; // uniform in [0, maxBWdem]
        }

        // demands
        List<List<Class<?>>> demClasses = new ArrayList<List<Class<?>>>();
        List<List<String[]>> demParamNames = new ArrayList<List<String[]>>();
        List<List<String[]>> demParamMaxValues = new ArrayList<List<String[]>>();

        // CPU demand
        for (int l = 0; l < numVNets; l++) {
            List<Class<?>> cls = new ArrayList<Class<?>>();
            cls.add(CpuDemand.class);
            demClasses.add(cls);
            List<String[]> param = new ArrayList<String[]>();
            param.add(new String[]{"demandedCycles"});
            demParamNames.add(param);
        }

        // BW demand
        for (int l = 0; l < numVNets; l++) {

            List<Class<?>> cls = new ArrayList<Class<?>>();
            cls.add(BandwidthDemand.class);
            demClasses.add(cls);
            List<String[]> param = new ArrayList<String[]>();
            param.add(new String[]{"demandedBandwidth"});
            demParamNames.add(param);
        }
        // the maximum values for both CPU and BW demand, again in arrays
        for (int l = 0; l < numVNets; l++) {
            List<String[]> maxValues = new ArrayList<String[]>();
            maxValues.add(new String[]{Double.toString(maxCPUdem[l])});
            maxValues.add(new String[]{Double.toString(maxBWdem[l])});
            demParamMaxValues.add(maxValues);
        }

        ConstraintsGeneratorDialog.generateConstraintsVirtual(demClasses,
                demParamNames, demParamMaxValues);

        // -----------------------

        // check if we get the desired rho
        substrate = ToolKit.getScenario().getNetworkStack().getSubstrate();
        double sumCPUResources = 0.0;
        for (SubstrateNode n : substrate.getVertices())
            for (AbstractResource res : n.get())
                if (res instanceof CpuResource)
                    sumCPUResources += ((CpuResource) res).getCycles();

        double sumBWResources = 0.0;
        for (SubstrateLink l : substrate.getEdges())
            for (AbstractResource res : l.get())
                if (res instanceof BandwidthResource)
                    sumBWResources += ((BandwidthResource) res).getBandwidth();

        double sumCPUDemands = 0;
        double maxCPUDemand = 0;
        double sumBWDemands = 0;
        double maxBWDemand = 0;
        for (int i = 1; ToolKit.getScenario().getNetworkStack().getLayer(i) != null; i++) {
            VirtualNetwork vn = (VirtualNetwork) ToolKit.getScenario()
                    .getNetworkStack().getLayer(i);
            for (VirtualNode n : vn.getVertices())
                for (AbstractDemand dem : n.get())
                    if (dem instanceof CpuDemand) {
                        sumCPUDemands += ((CpuDemand) dem).getDemandedCycles();
                        maxCPUDemand = Math.max(maxCPUDemand,
                                ((CpuDemand) dem).getDemandedCycles());
                    }

            for (VirtualLink l : vn.getEdges())
                for (AbstractDemand dem : l.get())
                    if (dem instanceof BandwidthDemand) {
                        sumBWDemands += ((BandwidthDemand) dem)
                                .getDemandedBandwidth();
                        maxBWDemand = Math.max(maxBWDemand,
                                ((BandwidthDemand) dem).getDemandedBandwidth());
                    }
        }

        System.out.println("Scenario generation complete.");

        try {
            FileWriter w = new FileWriter(basePath + File.separator + "README."
                    + suffix);
            w.write("rho = " + rho + "\n");
            w.write("CPU resources total = " + sumCPUResources + "\n");
            w.write("CPU demands total = " + sumCPUDemands + ", "
                    + sumCPUDemands / sumCPUResources + " of total resources."
                    + "\n");
            w.write("CPU max demand/max resource = " + maxCPUDemand + "/"
                    + maxCPUResources + "\n");
            w.write("BW resources total = " + sumBWResources + "\n");
            w.write("BW demands total = " + sumBWDemands + ", " + sumBWDemands
                    / sumBWResources + " of total resources." + "\n");
            w.write("BW max demand/max resource = " + maxBWDemand + "/"
                    + maxBWResources + "\n");
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
