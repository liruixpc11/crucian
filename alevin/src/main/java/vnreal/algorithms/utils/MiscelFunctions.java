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
package vnreal.algorithms.utils;

import org.apache.commons.collections15.Transformer;
import vnreal.constraints.AbstractConstraint;
import vnreal.demands.AbstractDemand;
import vnreal.demands.BandwidthDemand;
import vnreal.demands.CpuDemand;
import vnreal.network.Link;
import vnreal.network.Network;
import vnreal.network.NetworkStack;
import vnreal.network.Node;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Class with some util functions that are called from one or more virtual
 * network embedding algorithm
 *
 * @author Juan Felipe Botero
 */

public class MiscelFunctions {
    private static final DecimalFormat twoDForm = new DecimalFormat("#.##");

    /**
     * @param d number to round to two decimal places
     * @return rounded double value
     */
    public static Double roundTwoDecimals(double d) {
        return Double.valueOf(twoDForm.format(d));
    }

    private static final DecimalFormat threeDForm = new DecimalFormat("#.###");

    /**
     * @param d number to round to three decimal places
     * @return rounded double value
     */
    public static Double roundThreeDecimals(double d) {
        return Math.round(d * 1000) / 1000.0;
    }

    private static final DecimalFormat twelveDForm = new DecimalFormat(
            "#.############");

    /**
     * @param d number to round to twelve decimal places
     * @return rounded double value
     */
    public static double roundTwelveDecimals(double d) {
        return Double.valueOf(twelveDForm.format(d));
    }

    /**
     * Process the results of the LP solver to create an answer in a HashedMap
     * Structure with the substrate and virtual link and the LP solver answer.
     *
     * @param solverResult Result of the solver
     * @param value        variable of the solver that will be processed
     * @return Hashed map structure with organized solver results
     */
    public static Map<List<String>, Double> processSolverResult(
            Map<String, Double> solverResult, String value) {
        Map<List<String>, Double> newSolverResult = new LinkedHashMap<List<String>, Double>();

        int i;
        String word, subword;
        StringTokenizer elements, subElements;
        List<String> values, subValues = null;

        for (Iterator<String> cad = solverResult.keySet().iterator(); cad
                .hasNext(); ) {
            String ntmp = cad.next();
            Double vtmp = solverResult.get(ntmp);

            elements = new StringTokenizer(ntmp, value);
            values = new LinkedList<String>();

            while (elements.hasMoreTokens()) {
                word = elements.nextToken();
                i = 1;
                subElements = new StringTokenizer(word, ",");
                subValues = new LinkedList<String>();
                while (subElements.hasMoreTokens()) {
                    subword = subElements.nextToken();
                    subValues.add(subword);
                    i++;
                }
                values.addAll(subValues);
            }
            newSolverResult.put(values, vtmp);
        }
        return newSolverResult;
    }

    /**
     * @param vNet Virtual Network Request
     * @return revenue of vNet
     */
    public static double calculateVnetRevenue(VirtualNetwork vNet) {
        double total_demBW = 0;
        double total_demCPU = 0;
        Iterable<VirtualLink> tmpLinks;
        Iterable<VirtualNode> tmpNodes;
        tmpLinks = vNet.getEdges();
        tmpNodes = vNet.getVertices();
        for (Iterator<VirtualLink> tmpLink = tmpLinks.iterator(); tmpLink
                .hasNext(); ) {
            VirtualLink tmpl = tmpLink.next();
            for (AbstractDemand dem : tmpl) {
                if (dem instanceof BandwidthDemand) {
                    total_demBW += ((BandwidthDemand) dem)
                            .getDemandedBandwidth();
                    break; // continue with next link
                }
            }
        }
        for (Iterator<VirtualNode> tmpNode = tmpNodes.iterator(); tmpNode
                .hasNext(); ) {
            VirtualNode tmps = tmpNode.next();
            for (AbstractDemand dem : tmps) {
                if (dem instanceof CpuDemand) {
                    total_demCPU += ((CpuDemand) dem).getDemandedCycles();
                    break; // continue with next node
                }
            }
        }
        return (total_demBW + total_demCPU);
    }

    /**
     * Method to sort the set of virtual networks taking into account the revenues
     *
     * @param stack set of VNRs and substrate net
     * @return ordered VNR stack by revenues
     */
    @SuppressWarnings("unchecked")
    public static NetworkStack sortByRevenues(NetworkStack stack) {
        Map<Object, Double> Revenues = new LinkedHashMap<Object, Double>();
        Iterable networks = new LinkedList<Network>();
        double revenue;
        for (Iterator<Network<?, ?, ?>> net = stack.iterator(); net.hasNext(); ) {
            Network tmpN = net.next();
            if (tmpN.getLayer() != 0) {
                revenue = calculateVnetRevenue((VirtualNetwork) tmpN);
                Revenues.put(((VirtualNetwork) tmpN), revenue);
            }
        }
        Map sortedRevenues = sortByValue(Revenues);
        networks = sortedRevenues.keySet();

        List<VirtualNetwork> virtualNetworks = new LinkedList<VirtualNetwork>();
        for (Iterator<Network> n = networks.iterator(); n.hasNext(); ) {
            Network tmp = n.next();
            virtualNetworks.add((VirtualNetwork) tmp);
        }
        NetworkStack new_stack = new NetworkStack(stack.getSubstrate(),
                virtualNetworks);
        return new_stack;
    }

    /**
     * @param map
     * @return Ordered Map structure
     */
    @SuppressWarnings("unchecked")
    public static Map sortByValue(Map map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o2, Object o1) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });

        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static <T extends Comparable<T>, C extends AbstractConstraint, N extends Node<C>, L extends Link<C>>
    T findMinValueOfNode(Network<C, N, L> network, Transformer<N, T> transformer, T maxValue) {
        T minValue = maxValue;
        for (N node : network.getVertices()) {
            T current = transformer.transform(node);
            // minValue > current
            if (minValue.compareTo(current) > 0) {
                minValue = current;
            }
        }

        return minValue;
    }

    public static <T extends Comparable<T>, C extends AbstractConstraint, N extends Node<C>, L extends Link<C>>
    T findMinValueOfLink(Network<C, N, L> network, Transformer<L, T> transformer, T maxValue) {
        T minValue = maxValue;
        for (L node : network.getEdges()) {
            T current = transformer.transform(node);
            // minValue > current
            if (minValue.compareTo(current) > 0) {
                minValue = current;
            }
        }

        return minValue;
    }

    public static class MinMax<T extends Comparable<T>> {
        public T min;
        public T max;

        public MinMax(T min, T max) {
            this.min = min;
            this.max = max;
        }
    }

    public static <T extends Comparable<T>, C extends AbstractConstraint, N extends Node<C>, L extends Link<C>>
    MinMax<T> findMinMaxValueOfLink(Network<C, N, L> network, Transformer<L, T> transformer, T minValue, T maxValue) {
        // 把最小值设为可能的最大值，方便后面更新
        MinMax<T> minMax = new MinMax<T>(maxValue, minValue);
        for (L node : network.getEdges()) {
            T current = transformer.transform(node);
            // minValue > current
            if (minMax.min.compareTo(current) > 0) {
                minMax.min = current;
            }
            if (minMax.max.compareTo(current) < 0) {
                minMax.max = current;
            }
        }

        return minMax;
    }

    public static <T extends Comparable<T>, C extends AbstractConstraint, N extends Node<C>, L extends Link<C>>
    MinMax<T> findMinMaxValueOfNode(Network<C, N, L> network, Transformer<N, T> transformer, T minValue, T maxValue) {
        // 把最小值设为可能的最大值，方便后面更新
        MinMax<T> minMax = new MinMax<T>(maxValue, minValue);
        for (N node : network.getVertices()) {
            T current = transformer.transform(node);
            // minValue > current
            if (minMax.min.compareTo(current) > 0) {
                minMax.min = current;
            }
            if (minMax.max.compareTo(current) < 0) {
                minMax.max = current;
            }
        }

        return minMax;
    }
}
