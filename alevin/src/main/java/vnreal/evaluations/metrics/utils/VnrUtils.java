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
package vnreal.evaluations.metrics.utils;

import vnreal.demands.AbstractDemand;
import vnreal.demands.CpuDemand;
import vnreal.mapping.Mapping;
import vnreal.network.Network;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.CpuResource;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class VnrUtils {
    private VnrUtils() {
        // prevent object creation
    }

    @SuppressWarnings("unchecked")
    public static Map<VirtualNetwork, Boolean> calculateMappedVnr(
            NetworkStack stack) {
        VirtualNetwork tempVnr;
        Network tmpN;
        boolean isMapped;
        Map<VirtualNetwork, Boolean> isMappedVnr = new LinkedHashMap<VirtualNetwork, Boolean>();
        for (Iterator<Network<?, ?, ?>> net = stack.iterator(); net.hasNext(); ) {
            tmpN = net.next();
            if (tmpN.getLayer() != 0) {
                tempVnr = (VirtualNetwork) tmpN;
                isMapped = true;
                for (VirtualNode tmpVNode : tempVnr.getVertices()) {
                    for (AbstractDemand dem : tmpVNode) {
                        if (dem instanceof CpuDemand) {
                            if (dem.getMappings().isEmpty()) {
                                isMapped = false;
                            }
                            break;
                        }
                    }
                }
                isMappedVnr.put(tempVnr, isMapped);
            }
        }
        return isMappedVnr;
    }

    public static int numberMappedVnr(Map<VirtualNetwork, Boolean> isMappedVnr) {
        int numberMappedVnets = 0;
        VirtualNetwork tempVNet;

        for (Iterator<VirtualNetwork> itt = isMappedVnr.keySet().iterator(); itt
                .hasNext(); ) {
            tempVNet = itt.next();
            if (isMappedVnr.get(tempVNet))
                numberMappedVnets++;
        }
        return numberMappedVnets;
    }

    public static int getStressLevel(SubstrateNode n) {
        int max = 0;
        int stress;
        for (AbstractResource r : n.get()) {
            stress = 0;
            if (r instanceof CpuResource) {
                for (Mapping f : r.getMappings()) {
                    if (f.getDemand().getOwner() instanceof VirtualNode)
                        stress++;
                }
            } else {
                stress = r.getMappings().size();
            }
            if (stress > max) {
                max = stress;
            }
        }
        return max;
    }
}
