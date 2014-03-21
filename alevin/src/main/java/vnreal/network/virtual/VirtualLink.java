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
package vnreal.network.virtual;

import vnreal.constraints.ILinkConstraint;
import vnreal.demands.AbstractDemand;
import vnreal.network.Link;

import java.util.LinkedList;
import java.util.List;

/**
 * A virtual network link class.
 *
 * @author Michael Duelli
 * @author Vlad Singeorzan
 */
public class VirtualLink extends Link<AbstractDemand> {
    private final int layer;
    private final List<AbstractDemand> hhs;

    public VirtualLink(int id, int layer) {
        super(id);
        this.layer = layer;
        hhs = new LinkedList<AbstractDemand>();
    }

    public VirtualLink(int layer) {
        super();
        this.layer = layer;
        hhs = new LinkedList<AbstractDemand>();
    }

    public int getLayer() {
        return layer;
    }

    @Override
    public String toString() {
        return "VirtualLink(" + getId() + ")@" + getLayer();
    }

    @Override
    public String toStringShort() {
        return "VL(" + getId() + ")";
    }

    @Override
    protected boolean preAddCheck(AbstractDemand t) {
        // Only allow to add this type.
        // To implement multipath algorithms it is important that more than one
        // virtual link demand could be added to each virtual link (each demand
        // will be fulfilled by each path of the solution).
        if (t instanceof ILinkConstraint) {/*
                                             * if
											 * (!this.containsConstraintType(t))
											 * return true; else {
											 * System.err.println
											 * ("Cannot add constraint " + t +
											 * " to link " + this +
											 * " because it already has a constraint of this type."
											 * ); return false; }
											 */
            return true;
        } else {
            System.err.println("Cannot add non-ILinkConstraint " + t
                    + " to link " + this);
            return false;
        }
    }

    public void addHiddenHopDemand(AbstractDemand hh) {
        hhs.add(hh);
    }

    public List<AbstractDemand> getHiddenHopDemands() {
        return hhs;
    }

    public void clearHiddenHopDemands() {
        // Release and remove old hh demand before setting up a new one
        for (AbstractDemand hh : hhs)
            if (!hh.unregisterAll())
                throw new AssertionError();
        hhs.clear();
    }

    /**
     * 创建一个新的连接，并（浅）复制所有的约束
     * @return 新的连接
     */
    /*
    public VirtualLink copy() {
        VirtualLink virtualLink = new VirtualLink(getId(), getLayer());
        for (AbstractDemand demand : get()) {
            virtualLink.add(demand);
        }
        return virtualLink;
    }
    */
}
