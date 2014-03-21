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
package vnreal.network;

import mulavito.graph.AbstractLayerStack;
import vnreal.demands.AbstractDemand;
import vnreal.mapping.Mapping;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The stack of {@link Network}s = {@link VirtualNetwork}s built upon the
 * {@link SubstrateNetwork}.
 *
 * @author Michael Duelli
 * @since 2010-09-13
 */
public final class NetworkStack extends AbstractLayerStack<Network<?, ?, ?>> {
    private SubstrateNetwork substrate;

    public NetworkStack(SubstrateNetwork substrate, List<VirtualNetwork> vns) {
        this.substrate = substrate;
        addLayer(substrate);
        layers.addAll(vns);
    }

    public List<VirtualNetwork> getVirtualNetworks() {
        List<VirtualNetwork> virtualNetworks = new ArrayList<VirtualNetwork>();
        for (int i = 1; i < size(); i++) {
            virtualNetworks.add((VirtualNetwork) getLayer(i));
        }
        return Collections.unmodifiableList(virtualNetworks);
    }

    public SubstrateNetwork getSubstrate() {
        return substrate;
    }

    /**
     * @param layer The demanded layer id
     * @return The substrate network for layer 0, a virtual network for higher
     * layers or <code>null</code> if no such layer exists.
     */
    @SuppressWarnings("rawtypes")
    public Network getLayer(int layer) {
        if (layer > layers.size() - 1)
            return null;

        return layers.get(layer);
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("Network stack:\n");

        sb.append(substrate.getLabel() + "\n" + substrate.toString() + "\n");

        for (Network<?, ?, ?> n : this)
            sb.append(n.getLabel() + "\n" + n.toString() + "\n");

        return sb.toString();
    }

    /**
     * @return The number of networks including substrate and virtual networks.
     */
    public int size() {
        return layers.size();
    }

    public boolean hasMappings() {
        if (substrate != null) {
            for (SubstrateNode sn : substrate.getVertices()) {
                for (AbstractResource c : sn.get()) {
                    if (c.getMappings().size() > 0)
                        return true;
                }
            }
            for (SubstrateLink sl : substrate.getEdges()) {
                for (AbstractResource c : sl.get()) {
                    if (c.getMappings().size() > 0)
                        return true;
                }
            }
            return false;
        } else
            throw new AssertionError("Substrate network is null.");
    }

    /**
     * unregister all mappings by iterating over the substrate
     */
    public void clearMappings() {
        for (@SuppressWarnings("rawtypes")
        Network n : this) {
            if (n instanceof VirtualNetwork)
                clearVnrMappings((VirtualNetwork) n);
        }
        fireStateChanged();
    }

    /**
     * unregister mapping of a virtual network that could not be mapped
     */
    public void clearVnrMappings(VirtualNetwork vNet) {
        for (VirtualNode node : vNet.getVertices()) {
            for (AbstractDemand dem : node.get()) {
                List<Mapping> mappingsCopy = new ArrayList<Mapping>();
                mappingsCopy.addAll(dem.getMappings());
                for (Mapping m : mappingsCopy)
                    m.getDemand().free(m.getResource());
            }
        }
        for (VirtualLink link : vNet.getEdges()) {
            for (AbstractDemand dem : link.get()) {
                List<Mapping> mappingsCopy = new ArrayList<Mapping>();
                mappingsCopy.addAll(dem.getMappings());
                for (Mapping m : mappingsCopy)
                    m.getDemand().free(m.getResource());
            }
            for (AbstractDemand dem : link.getHiddenHopDemands()) {
                List<Mapping> mappingsCopy = new ArrayList<Mapping>();
                mappingsCopy.addAll(dem.getMappings());
                for (Mapping m : mappingsCopy)
                    m.getDemand().free(m.getResource());
            }
        }
        fireStateChanged();
    }

    public void clearConstraints() {
        SubstrateNetwork sn = substrate;
        for (SubstrateNode n : sn.getVertices())
            n.removeAll();
        for (SubstrateLink l : sn.getEdges())
            l.removeAll();

        for (int i = 1; i < size(); i++) {
            VirtualNetwork vn = (VirtualNetwork) getLayer(i);

            for (VirtualNode n : vn.getVertices())
                n.removeAll();
            for (VirtualLink l : vn.getEdges())
                l.removeAll();
        }
    }
}
