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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import vnreal.ExchangeParameter;
import vnreal.ToolKit;
import vnreal.demands.AbstractDemand;
import vnreal.mapping.Mapping;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

/**
 * Exports a given scenario to an XML file using the same format as the
 * {@link ScenarioImporter}, i.e. the exported Document conforms to the file
 * <tt>scenario.dtd</tt>.
 *
 * @author Vlad Singeorzan
 * @author Michael Duelli
 * @since 2010-10-13
 */
public final class ScenarioExporter {
    /**
     * The NetworkStack to export.
     */
    private NetworkStack networkStack;

    /**
     * The XML document to be created.
     */
    private Document doc;

    /**
     * The OutputStream to write to.
     */
    private OutputStream out;

    public ScenarioExporter(OutputStream xmlFile) {
        out = xmlFile;
        networkStack = ToolKit.getScenario().getNetworkStack();
        init();
    }

    public ScenarioExporter(String filename) throws FileNotFoundException {
        this(new FileOutputStream(new File(filename)));
    }

    /**
     * Builds the XML document.
     *
     * @return true if successful, false otherwise
     */
    public boolean init() {
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .newDocument();
            Element root = doc.createElement("Scenario");
            doc.appendChild(root);

            root.appendChild(createSubstrateNetwork());
            root.appendChild(createVirtualNetworks());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void write() {
        if (doc != null) {
            try {
                writeToXML(out, doc);
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
                throw new AssertionError(
                        "Exeception occurd while attempting to write data to the XML file.");
            }
        } else {
            throw new AssertionError("Scenario Exporter not intialized.");
        }
    }

    /**
     * Creates the <SubstrateNetwork> XML node containing the
     * {@link SubstrateNetwork}as specified in the {@link NetworkStack}.
     *
     * @return the node containing the {@link SubstrateNetwork}.
     */
    private Node createSubstrateNetwork() {
        Element sn = doc.createElement("SubstrateNetwork");
        Element nodeEl;
        Element linkEl;
        // create nodes
        Element nodes = doc.createElement("SubstrateNodes");
        sn.appendChild(nodes);
        for (SubstrateNode n : networkStack.getSubstrate().getVertices()) {
            nodeEl = doc.createElement("SubstrateNode");
            nodeEl.setAttribute("id", Integer.toString(n.getId()));
            try {
                for (Method m : n.getClass().getMethods()) {
                    // sort out unused methods starting with "get"
                    if (isNeededGetter(m)) {
                        nodeEl.setAttribute(m.getName().substring(3, 4)
                                .toLowerCase()
                                + m.getName().substring(4), m.invoke(n)
                                .toString());
                    }
                }
                // add resources
                for (AbstractResource res : n.get()) {
                    nodeEl.appendChild(getResource(res));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            nodes.appendChild(nodeEl);
        }

        // create links
        Element links = doc.createElement("SubstrateLinks");
        sn.appendChild(links);
        for (SubstrateLink link : networkStack.getSubstrate().getEdges()) {
            linkEl = doc.createElement("SubstrateLink");
            linkEl.setAttribute("id", Integer.toString(link.getId()));
            // source & destination
            linkEl.setAttribute("source", Integer.toString(networkStack
                    .getSubstrate().getSource(link).getId()));
            linkEl.setAttribute("destination", Integer.toString(networkStack
                    .getSubstrate().getDest(link).getId()));
            try {
                // at the moment no getters for links
                for (Method m : link.getClass().getMethods()) {
                    if (isNeededGetter(m)) {
                        linkEl.setAttribute(m.getName().substring(3, 4)
                                .toLowerCase()
                                + m.getName().substring(4), m.invoke(link)
                                .toString());
                    }
                }
                // add resources
                for (AbstractResource res : link.get()) {
                    linkEl.appendChild(getResource(res));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            links.appendChild(linkEl);
        }

        return sn;
    }

    /**
     * Creates the <VirtualNetworks> XML node containing the
     * {@link VirtualNetwork}s as specified in the {@link NetworkStack}.
     *
     * @return the node containing the {@link VirtualNetworks}.
     */
    private Node createVirtualNetworks() {
        Element vns = doc.createElement("VirtualNetworks");
        Element nodeEl;
        Element linkEl;
        Element vn;
        // create virtual networks
        int i = 1;
        for (VirtualNetwork layer = (VirtualNetwork) networkStack.getLayer(i); layer != null; layer = (VirtualNetwork) networkStack
                .getLayer(++i)) {
            vn = doc.createElement("VirtualNetwork");
            vn.setAttribute("layer", Integer.toString(i));
            // create nodes
            Element nodes = doc.createElement("VirtualNodes");
            vn.appendChild(nodes);
            for (VirtualNode n : layer.getVertices()) {
                nodeEl = doc.createElement("VirtualNode");
                nodeEl.setAttribute("id", Integer.toString(n.getId()));
                try {
                    for (Method m : n.getClass().getMethods()) {
                        if (isNeededGetter(m)) {
                            nodeEl.setAttribute(m.getName().substring(3, 4)
                                    .toLowerCase()
                                    + m.getName().substring(4), m.invoke(n)
                                    .toString());
                        }
                    }
                    // add demands and mappings if available
                    for (AbstractDemand dem : n.get()) {
                        nodeEl.appendChild(getDemand(dem));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                nodes.appendChild(nodeEl);
            }

            // create links
            Element links = doc.createElement("VirtualLinks");
            vn.appendChild(links);
            for (VirtualLink link : layer.getEdges()) {
                linkEl = doc.createElement("VirtualLink");
                linkEl.setAttribute("id", Integer.toString(link.getId()));
                // source & destination
                linkEl.setAttribute("source", Integer
                        .toString(((VirtualNode) ((VirtualNetwork) networkStack
                                .getLayer(link.getLayer())).getSource(link))
                                .getId()));
                linkEl.setAttribute("destination", Integer
                        .toString(((VirtualNode) ((VirtualNetwork) networkStack
                                .getLayer(link.getLayer())).getDest(link))
                                .getId()));
                try {
                    for (Method m : link.getClass().getMethods()) {
                        if (isNeededGetter(m)) {
                            linkEl.setAttribute(m.getName().substring(3, 4)
                                    .toLowerCase()
                                    + m.getName().substring(4), m.invoke(link)
                                    .toString());
                        }
                    }
                    // add demands and mappings if available
                    for (AbstractDemand dem : link.get()) {
                        linkEl.appendChild(getDemand(dem));
                    }
                    // add HH Demands and mappings
                    for (AbstractDemand hhDem : link.getHiddenHopDemands()) {
                        linkEl.appendChild(getHHDemand(hhDem));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                links.appendChild(linkEl);
            }
            vns.appendChild(vn);
        }
        return vns;
    }

    /**
     * Writes the created XML document on the given {@link OutputStream}
     *
     * @param out the {@link OutputStream} to write to.
     * @param doc the Document to be written
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    private void writeToXML(OutputStream out, Document doc)
            throws TransformerFactoryConfigurationError,
            TransformerConfigurationException, TransformerException {
        Transformer transformer = TransformerFactory.newInstance()
                .newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer
                .setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "scenario.dtd");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}"
                + "indent-amount", "2");
        transformer.transform(new DOMSource(doc), new StreamResult(out));
    }

    private static String getMethodReturn(Method m, Object o) {
        if (m.getReturnType().getSimpleName().equalsIgnoreCase("Double")) {
            try {
                return Double.toString((Double) m.invoke(o));
            } catch (Exception e) {
                e.printStackTrace();
                throw new AssertionError("Cannot invoke method.");
            }
        } else if (m.getReturnType().getSimpleName().equalsIgnoreCase("String")) {
            try {
                return m.invoke(o).toString();
            } catch (Exception e) {
                e.printStackTrace();
                throw new AssertionError("Cannot invoke method.");
            }
        } else if (m.getReturnType().getSimpleName().equals("Integer")) {
            try {
                return Integer.toString((Integer) m.invoke(o));
            } catch (Exception e) {
                e.printStackTrace();
                throw new AssertionError("Cannot invoke method.");
            }
        } else if (m.getReturnType().getSimpleName().equals("Boolean")) {
            try {
                return Boolean.toString((Boolean) m.invoke(o));
            } catch (Exception e) {
                e.printStackTrace();
                throw new AssertionError("Cannot invoke method.");
            }
        } else
            throw new AssertionError("Invalid method return type: "
                    + m.getReturnType().getSimpleName() + "; method: "
                    + m.getName());
    }

    /**
     * Creates a <Resource> XML element.
     *
     * @param res the {@link AbstractResource} to create the element from.
     * @return the created element.
     */
    private Element getResource(AbstractResource res) {
        Element resourceEl = doc.createElement("Resource");
        Element paramEl;
        resourceEl.setAttribute("type", res.getClass().getSimpleName());
        for (Method m : res.getClass().getDeclaredMethods()) {
            if (isNeededGetter(m)) {
                paramEl = doc.createElement("Parameter");
                paramEl.setAttribute("name", m.getName().substring(3));
                paramEl.setAttribute("type", m.getReturnType().getSimpleName());
                paramEl.setAttribute("value", getMethodReturn(m, res));
                resourceEl.appendChild(paramEl);
            }
        }
        return resourceEl;
    }

    /**
     * Creates a <Demand> XML element.
     *
     * @param dem  the {@link AbstractDemand} to create the element from. This
     *             can also be a HH Demand.
     * @param name The element name. This should be either "Demand" or
     *             "HiddenHopDemand"
     * @return the created element.
     */
    private Node getDemand(AbstractDemand dem, String name) {
        Element demandEl = doc.createElement(name);
        Element paramEl;
        Element mappingEl;
        demandEl.setAttribute("type", dem.getClass().getSimpleName());
        for (Method m : dem.getClass().getDeclaredMethods()) {
            if (isNeededGetter(m)) {
                paramEl = doc.createElement("Parameter");
                paramEl.setAttribute("name", m.getName().substring(3));
                paramEl.setAttribute("type", m.getReturnType().getSimpleName());
                paramEl.setAttribute("value", getMethodReturn(m, dem));
                demandEl.appendChild(paramEl);
            }
        }
        for (Mapping mapping : dem.getMappings()) {
            mappingEl = doc.createElement("Mapping");
            mappingEl.setAttribute("substrateEntity", Integer.toString(mapping
                    .getResource().getOwner().getId()));
            mappingEl.setAttribute("resourceType", mapping.getResource()
                    .getClass().getSimpleName());
            demandEl.appendChild(mappingEl);
        }
        return demandEl;
    }

    private Node getDemand(AbstractDemand dem) {
        return getDemand(dem, "Demand");
    }

    private Node getHHDemand(AbstractDemand hhDem) {
        return getDemand(hhDem, "HiddenHopDemand");
    }

    /**
     * Used to determine the getters that are needed to build the XML document.
     *
     * @param name the method name.
     * @return true if the getter is needed for the XML document, false
     * otherwise.
     */
    private static boolean isNeededGetter(Method m) {
        if (m.isAnnotationPresent(ExchangeParameter.class)
                && m.getName().startsWith("get")) {
            return true;
        }
        return false;
    }
}
