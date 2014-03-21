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
package vnreal.resources;

import vnreal.constraints.AbstractConstraint;
import vnreal.constraints.INodeConstraint;
import vnreal.network.NetworkEntity;
import vnreal.network.Node;
import vnreal.network.virtual.VirtualNode;

public class MultiCoreEnergyResource extends EnergyResource implements
        INodeConstraint {

    int idleConsumption;
    int additionalConsumptionPerCore;
    int numberOfCores;

    public MultiCoreEnergyResource(Node<? extends AbstractConstraint> owner,
                                   int idleConsumption, int additionalConsumptionPerCore,
                                   int numberOfCores) {

        super(owner);
        this.idleConsumption = idleConsumption;
        this.additionalConsumptionPerCore = additionalConsumptionPerCore;
        this.numberOfCores = numberOfCores;
    }

    @Override
    public int getIdleConsumption() {
        return idleConsumption;
    }

    @Override
    public int getCurrentConsumption() {
        if (!isUsed()) {
            return 0;
        }

        int size = usedBy();
        int mult = size >= numberOfCores ? numberOfCores : size;
        return idleConsumption + mult * additionalConsumptionPerCore;
    }

    @Override
    public int getConsumptionAfterMapping(VirtualNode vn) {
        int size = usedBy() + 1;
        int mult = size >= numberOfCores ? numberOfCores : size;
        return idleConsumption + mult * additionalConsumptionPerCore;
    }

    @Override
    public String toString() {
        return "[" + super.toString()
                + ";MultiCoreEnergyConsumption (idleConsumption="
                + idleConsumption + ", additionalConsumptionPerCore="
                + additionalConsumptionPerCore + ", numberOfCores="
                + numberOfCores + ")]";
    }

    @Override
    public AbstractResource getCopy(NetworkEntity<? extends AbstractConstraint> owner) {

        MultiCoreEnergyResource clone = new MultiCoreEnergyResource(
                (Node<? extends AbstractConstraint>) owner,
                idleConsumption, additionalConsumptionPerCore, numberOfCores);
        clone.usedBy = usedBy;

        return clone;
    }
}
