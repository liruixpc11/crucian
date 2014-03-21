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

import vnreal.constraints.AbstractConstraint;

import java.util.*;

/**
 * The building blocks of a network: either node or link.
 *
 * @param <T> The constraint parameter, either resource or demand.
 * @author Michael Duelli
 */
public abstract class NetworkEntity<T extends AbstractConstraint> implements
        Iterable<T> {
    private final List<T> constraints = new LinkedList<T>();

    private static final SortedSet<Integer> uniqueIds = new TreeSet<Integer>();

    public static void resetIds() {
        uniqueIds.clear();
    }

    public static int allocateId() {
        return uniqueIds.isEmpty() ? 0 : uniqueIds.last() + 1;
    }

    private final int id;

    /**
     * Used in GUI methods.
     */
    protected NetworkEntity() {
        this(uniqueIds.isEmpty() ? 0 : uniqueIds.last() + 1);
    }

    protected NetworkEntity(Integer id) {
        if (uniqueIds.contains(id))
            throw new AssertionError("Network entity id " + id + " not unique.");
        uniqueIds.add(id);
        this.id = id;
    }

    /**
     * Add a constraint.
     *
     * @param t The considered constraint.
     * @return true on success, false otherwise.
     */
    public boolean add(T t) {
        if (preAddCheck(t))
            if (t.getOwner() == this)
                return constraints.add(t);
            else {
                System.err.println("Cannot add constraint " + t + " to entity "
                        + this + " because owner != " + this);
                return false;
            }
        return false;
    }

    protected abstract boolean preAddCheck(T t);

    /**
     * Remove a constraint.
     *
     * @param t The considered constraint.
     * @return true on success, false otherwise.
     */
    public final boolean remove(T t) {
        if (constraints.size() > 1) {
            return t.unregisterAll() && constraints.remove(t);
        }
        System.err.println("Cannot remove constraint " + t + " from entity "
                + this + " because number of constraints is 1.");
        return false;
    }

    /**
     * Get constraints of specific type
     *
     * @param type constraint type
     * @return list of constraint
     */
    @SuppressWarnings("unchecked")
    public final <U extends T> List<U> get(Class<U> type) {
        List<U> constraintsOfType = new ArrayList<U>();
        for (T t : constraints) {
            if (type.isAssignableFrom(t.getClass())) {
                constraintsOfType.add((U) t);
            }
        }

        return constraintsOfType;
    }

    public final <U extends T> U getSingle(Class<U> type) {
        for (T t : constraints) {
            if (type.isAssignableFrom(t.getClass())) {
                return (U) t;
            }
        }

        return null;
    }

    public final void removeAll() {
        for (AbstractConstraint c : constraints) {
            c.unregisterAll();
        }
        constraints.clear();
    }

    public final List<T> get() {
        return Collections.unmodifiableList(constraints);
    }

    @Override
    public final Iterator<T> iterator() {
        return constraints.iterator();
    }

    public final int getId() {
        return id;
    }

    public abstract String toStringShort();
}
