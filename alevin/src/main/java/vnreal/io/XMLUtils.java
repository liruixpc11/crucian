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
package vnreal.io;

import org.w3c.dom.Node;

/**
 * A class of static methods useful for processing XML files.
 *
 * @author Michael Duelli
 */
public final class XMLUtils {
    public static void xmlDebug(Node n) {
        System.err.println("NodeName: '" + n.getNodeName() + "'");
        System.err.println("NodeValue: '" + n.getNodeValue() + "'");
    }

    public static boolean isElementNode(Node n) {
        return (n.getNodeType() == Node.ELEMENT_NODE);
    }

    /**
     * @param n The current XML node
     * @return The next sibling that is a {@link Node#ELEMENT_NODE}.
     */
    public static Node nextElementNode(Node n) {
        if (isElementNode(n))
            n = n.getNextSibling();

        while (n != null && !isElementNode(n))
            n = n.getNextSibling();

        return n;
    }

    public static Node firstElementNodeChild(Node n) {
        Node m = n.getFirstChild();

        while (m != null && !isElementNode(m))
            m = m.getNextSibling();

        return m;
    }

    public static String getAttribute(Node n, String s) {
        return n.getAttributes().getNamedItem(s).getTextContent();
    }

    public static int getIntAttribute(Node n, String s) {
        return Integer.parseInt(getAttribute(n, s));
    }

    public static double getDoubleAttribute(Node n, String s) {
        return Double.parseDouble(getAttribute(n, s));
    }

    public static boolean getBooleanAttribute(Node n, String s) {
        return Boolean.parseBoolean(getAttribute(n, s));
    }
}
