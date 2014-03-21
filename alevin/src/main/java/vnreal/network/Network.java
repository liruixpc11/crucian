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

import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.ObservableGraph;
import mulavito.graph.ILayer;
import vnreal.constraints.AbstractConstraint;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public abstract class Network<T extends AbstractConstraint, V extends Node<T>, E extends Link<T>>
        extends ObservableGraph<V, E> implements ILayer<V, E> {

    private boolean autoUnregisterConstraints = true;

    protected Network() {
        super(new DirectedOrderedSparseMultigraph<V, E>());
    }

    protected Network(boolean autoUnregisterConstraints) {
        this();
        this.autoUnregisterConstraints = autoUnregisterConstraints;
    }

    @Override
    public boolean removeVertex(V v) {
        if (autoUnregisterConstraints) {
            // unregister all mappings first
            boolean unregistered = true;
            List<T> constraints = v.get();
            for (T cons : constraints)
                unregistered = unregistered && cons.unregisterAll();
            return (unregistered && super.removeVertex(v));
        } else {
            return super.removeVertex(v);
        }
    }

    @Override
    public boolean removeEdge(E e) {
        if (autoUnregisterConstraints) {
            // unregister all mappings first
            boolean unregistered = true;
            List<T> constraints = e.get();
            for (T cons : constraints)
                unregistered = unregistered && cons.unregisterAll();
            return (unregistered && super.removeEdge(e));
        } else {
            return super.removeEdge(e);
        }
    }

    public List<V> getAdjacentNodes(V v) {
        List<V> vertices = new ArrayList<>();
        for (E edge : getIncidentEdges(v)) {
            V source = getSource(edge);
            V dest = getDest(edge);
            if (source != v) {
                vertices.add(source);
            } else if (dest != v) {
                vertices.add(dest);
            }
        }
        return vertices;
    }

    public abstract Network<T, V, E> getInstance(boolean autoUnregister);

    public abstract Network<T, V, E> getCopy(boolean autoUnregister);

}
