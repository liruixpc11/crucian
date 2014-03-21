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
import org.w3c.dom.Node;
import org.xml.sax.*;
import vnreal.ExchangeParameter;
import vnreal.ToolKit;
import vnreal.demands.AbstractDemand;
import vnreal.network.NetworkEntity;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Imports a {@link NetworkStack} (consisting of the {@link SubstrateNework} and
 * a list of {@link VirtualNetwork}s) from an XML file that conforms to the file
 * <tt>scenario.dtd</tt>.
 *
 * @author Vlad Singeorzan
 * @author Michael Duelli
 * @since 2010-10-01
 */
public final class ScenarioImporter {
    /**
     * The XML document.
     */
    private Document doc;

    public ScenarioImporter(String filename) throws FileNotFoundException {
        this(new FileInputStream(new File(filename)));
    }

    public ScenarioImporter(InputStream xmlFile) {
        init(xmlFile);
    }

    /**
     * Initializes the Importer, determining if the XML file conforms to the
     * DTD.
     *
     * @param xmlFile
     * @return true if the initialization was successful, false otherwise.
     */
    private boolean init(InputStream xmlFile) {
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setValidating(true);
            DocumentBuilder builder = f.newDocumentBuilder();
            builder.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception)
                        throws SAXException {
                    exception.printStackTrace();
                }

                @Override
                public void fatalError(SAXParseException arg0)
                        throws SAXException {
                    arg0.printStackTrace();
                }

                @Override
                public void error(SAXParseException arg0) throws SAXException {
                    arg0.printStackTrace();
                }
            });

            // Resolver that loads DTD from a stream.
            builder.setEntityResolver(new EntityResolver() {
                @Override
                public InputSource resolveEntity(String publicId,
                                                 String systemId) throws SAXException, IOException {
                    if (systemId.endsWith("scenario.dtd")) {
                        return new InputSource(getClass().getClassLoader().getResourceAsStream("XML/scenario.dtd"));
                    } else
                        return null;
                }
            });

            this.doc = builder.parse(xmlFile);

            return true;
        } catch (SAXException e) {
            System.err.println("ERROR: Chosen file is not a valid XML-file.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("ERROR: XML-file not found or access denied.");
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean isInitialized() {
        return doc != null;
    }

    /**
     * Method used to return the {@link NetworkStack} read from the XML file.
     */
    public void setNetworkStack() {
        if (isInitialized()) {
            Node root = null;
            // Skip DTD declaration and comments.
            for (int i = 0; i < doc.getChildNodes().getLength(); i++)
                if (XMLUtils.isElementNode(doc.getChildNodes().item(i))) {
                    root = doc.getChildNodes().item(i);
                    break;
                }

            if (root == null)
                throw new AssertionError();

            Node substrateNetworkNode = XMLUtils.firstElementNodeChild(root);
            Node virtualNetworksNode = XMLUtils
                    .nextElementNode(substrateNetworkNode);

			/*
             * To have a network stack which accepts substrate and virtual
			 * networks, we intentionally omit generics here.
			 * 
			 * We start with an empty stack, and add each node/link as it is
			 * created, so we can always access the elements already added. This
			 * is useful for the id uniqueness test in the IdResource class.
			 */
            ToolKit.getScenario().setNetworkStack(
                    new NetworkStack(new SubstrateNetwork(),
                            new ArrayList<VirtualNetwork>()));
            processSubstrateNetwork(substrateNetworkNode);
            processVirtualNetworks(virtualNetworksNode);
        } else {
            System.err.println("ERROR: XML Importer initialization failed.");
            throw new Error();
        }
    }

    /**
     * Creates a {@link SubstrateNetwork} as specified in the XML file.
     *
     * @param n the substrate network Node.
     */
    private void processSubstrateNetwork(Node n) {
        Node substrateNodes = XMLUtils.firstElementNodeChild(n);
        Node substrateLinks = XMLUtils.nextElementNode(substrateNodes);
        SubstrateNode substrateNode;
        int id;

        // create substrate nodes
        for (Node sNode = XMLUtils.firstElementNodeChild(substrateNodes); sNode != null; sNode = XMLUtils
                .nextElementNode(sNode)) {
            id = XMLUtils.getIntAttribute(sNode, "id");
            substrateNode = new SubstrateNode(id);
            substrateNode.setCoordinateX(XMLUtils.getDoubleAttribute(sNode,
                    "coordinateX"));
            substrateNode.setCoordinateY(XMLUtils.getDoubleAttribute(sNode,
                    "coordinateY"));

            for (Node res = XMLUtils.firstElementNodeChild(sNode); res != null; res = XMLUtils
                    .nextElementNode(res))
                substrateNode.add(getResource(res, substrateNode));

            if (!ToolKit.getScenario().getNetworkStack().getSubstrate()
                    .addVertex(substrateNode))
                throw new AssertionError("Failed to add Node  " + id
                        + "  to substrate network");
        }

        // create substrate links
        for (Node sLink = XMLUtils.firstElementNodeChild(substrateLinks); sLink != null; sLink = XMLUtils
                .nextElementNode(sLink)) {
            id = XMLUtils.getIntAttribute(sLink, "id");
            Integer source = XMLUtils.getIntAttribute(sLink, "source");
            Integer destination = XMLUtils
                    .getIntAttribute(sLink, "destination");

            SubstrateLink substrateLink = new SubstrateLink(id);
            if (!ToolKit
                    .getScenario()
                    .getNetworkStack()
                    .getSubstrate()
                    .addEdge(substrateLink, getSubstrateNodeForId(source),
                            getSubstrateNodeForId(destination)))
                throw new AssertionError("Failed to add Edge " + id
                        + " to substrate network");

            for (Node res = XMLUtils.firstElementNodeChild(sLink); res != null; res = XMLUtils
                    .nextElementNode(res))
                substrateLink.add(getResource(res, substrateLink));
        }
    }

    /**
     * Creates a list of {@link VirtualNetwork}s as specified in the XML file.
     *
     * @param n the {@link VirtualNetwork}'s Node.
     */
    private void processVirtualNetworks(Node n) {
        int layer;
        int id;
        VirtualNode virtualNode;

        for (Node virtualNet = XMLUtils.firstElementNodeChild(n); virtualNet != null; virtualNet = XMLUtils
                .nextElementNode(virtualNet)) {
            layer = XMLUtils.getIntAttribute(virtualNet, "layer");
            ToolKit.getScenario().getNetworkStack()
                    .addLayer(new VirtualNetwork(layer));

            Node virtualNodes = XMLUtils.firstElementNodeChild(virtualNet);
            Node virtualLinks = XMLUtils.nextElementNode(virtualNodes);

            // create virtual nodes
            for (Node vNode = XMLUtils.firstElementNodeChild(virtualNodes); vNode != null; vNode = XMLUtils
                    .nextElementNode(vNode)) {
                id = XMLUtils.getIntAttribute(vNode, "id");
                virtualNode = new VirtualNode(id, layer);
                virtualNode.setCoordinateX(XMLUtils.getDoubleAttribute(vNode,
                        "coordinateX"));
                virtualNode.setCoordinateY(XMLUtils.getDoubleAttribute(vNode,
                        "coordinateY"));

                if (!((VirtualNetwork) ToolKit.getScenario().getNetworkStack()
                        .getLayer(layer)).addVertex(virtualNode)) {
                    System.err.println("Failed to add Node  " + id
                            + "  to virtual network");
                    throw new Error();
                }

                for (Node dem = XMLUtils.firstElementNodeChild(vNode); dem != null; dem = XMLUtils
                        .nextElementNode(dem)) {
                    // add the demand and the mappings
                    virtualNode.add(getDemand(dem, virtualNode));
                }
            }

            // create virtual links
            for (Node vLink = XMLUtils.firstElementNodeChild(virtualLinks); vLink != null; vLink = XMLUtils
                    .nextElementNode(vLink)) {

                id = XMLUtils.getIntAttribute(vLink, "id");
                Integer source = XMLUtils.getIntAttribute(vLink, "source");
                Integer destination = XMLUtils.getIntAttribute(vLink,
                        "destination");

                VirtualLink virtualLink = new VirtualLink(id, layer);
                if (!((VirtualNetwork) ToolKit.getScenario().getNetworkStack()
                        .getLayer(layer)).addEdge(virtualLink,
                        getVirtualNodeForId(layer, source),
                        getVirtualNodeForId(layer, destination))) {
                    System.err.println("Failed to add Link  " + id
                            + "  to virtual network");
                    throw new Error();
                }

                for (Node dem = XMLUtils.firstElementNodeChild(vLink); dem != null; dem = XMLUtils
                        .nextElementNode(dem)) {
                    if (dem.getNodeName().equals("Demand"))
                        // add the demand and the mappings
                        virtualLink.add(getDemand(dem, virtualLink));
                    else if (dem.getNodeName().equals("HiddenHopDemand"))
                        // also add HH Demands if present
                        virtualLink.addHiddenHopDemand(getDemand(dem,
                                virtualLink));
                }
            }
        }
    }

    /**
     * Returns the value specified as a String as an Object of the given type.
     * Currently only Integer and String supported.
     *
     * @param type  The Object type.
     * @param value The Object value.
     * @return An Object with the specified type and value.
     */
    private Object getParamValue(String type, String value) {
        if (type.equalsIgnoreCase("java.lang.double")) {
            return Double.parseDouble(value);
        } else if (type.equalsIgnoreCase("java.lang.string")) {
            return value;
        } else if (type.equalsIgnoreCase("java.lang.integer")) {
            return Integer.parseInt(value);
        } else if (type.equalsIgnoreCase("java.lang.boolean")) {
            return Boolean.parseBoolean(value);
        } else {
            System.err.println("ERROR: Invalid parameter type.");
            throw new AssertionError();
        }
    }

    /**
     * Creates a {@link AbstractResource} as specified in the given XML
     * <Resource> Node.
     *
     * @param resource the XML Node of the Resource to be read.
     * @return the created {@link AbstractResource}
     */
    private AbstractResource getResource(Node resource,
                                         @SuppressWarnings("rawtypes") NetworkEntity owner) {
        try {
            Class<? extends Object> resClass;
            if (XMLUtils.getAttribute(resource, "type").indexOf(".") < 0) {
                resClass = Class.forName("vnreal.resources."
                        + XMLUtils.getAttribute(resource, "type"));
            } else {
                resClass = Class.forName(XMLUtils
                        .getAttribute(resource, "type"));
            }
            // we use getSuperclass() here, because the constructors have
            // arguments of type Node or Link, and the method is called with
            // arguments of type SubstrateNode or SubstrateLink
            AbstractResource res = (AbstractResource) resClass.getConstructor(
                    owner.getClass().getSuperclass()).newInstance(owner);
            Method[] methods = res.getClass().getMethods();
            for (Node param = XMLUtils.firstElementNodeChild(resource); param != null; param = XMLUtils
                    .nextElementNode(param)) {
                String paramName = XMLUtils.getAttribute(param, "name");
                String paramType = XMLUtils.getAttribute(param, "type");
                if (paramType.indexOf(".") < 0) {
                    paramType = "java.lang." + paramType;
                }
                String paramValue = XMLUtils.getAttribute(param, "value");
                Method setter = getSetterIgnoreCase(methods, paramName);
                setter.invoke(res, getParamValue(paramType, paramValue));
            }

            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Creates a {@link AbstractDemand} as specified in the given XML <Demand>
     * Node including the Mappings.
     *
     * @param demand the XML Node of the Demand to be read.
     * @return the created {@link AbstractDemand}
     */
    private AbstractDemand getDemand(Node demand,
                                     @SuppressWarnings("rawtypes") NetworkEntity owner) {
        try {
            Class<? extends Object> demClass;
            if (XMLUtils.getAttribute(demand, "type").indexOf(".") < 0) {
                demClass = Class.forName("vnreal.demands."
                        + XMLUtils.getAttribute(demand, "type"));
            } else {
                demClass = Class.forName(XMLUtils.getAttribute(demand, "type"));
            }
            // we use getSuperclass() here, because the constructors have
            // arguments of type Node or Link, and the method is called with
            // arguments of type VirtualNode or VirtualLink
            AbstractDemand dem = (AbstractDemand) demClass.getConstructor(
                    owner.getClass().getSuperclass()).newInstance(owner);
            Method[] methods = dem.getClass().getMethods();
            for (Node child = XMLUtils.firstElementNodeChild(demand); child != null; child = XMLUtils
                    .nextElementNode(child)) {
                if (child.getNodeName().equals("Parameter")) {
                    String paramName = XMLUtils.getAttribute(child, "name");
                    String paramType = XMLUtils.getAttribute(child, "type");
                    if (paramType.indexOf(".") < 0) {
                        paramType = "java.lang." + paramType;
                    }
                    String paramValue = XMLUtils.getAttribute(child, "value");
                    Method setter = getSetterIgnoreCase(methods, paramName);
                    setter.invoke(dem, getParamValue(paramType, paramValue));
                } else if (child.getNodeName().equals("Mapping")) {
                    int substrateId = XMLUtils.getIntAttribute(child,
                            "substrateEntity");
                    String resType = XMLUtils.getAttribute(child,
                            "resourceType");
                    AbstractResource res = getResource(substrateId, resType);
                    if (res.accepts(dem)) {
                        if (!dem.occupy(res))
                            throw new AssertionError(
                                    "Inconsistent mapping. The demand " + dem
                                            + " of network entity "
                                            + dem.getOwner().getId()
                                            + " cannot occupy the resource "
                                            + res + " of network entity "
                                            + res.getOwner().getId() + ".");
                    } else
                        throw new AssertionError(
                                "Inconsistent mapping. The resource " + res
                                        + " cannot fulfill the demand " + dem
                                        + ".");
                }
            }
            return dem;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Method getSetterIgnoreCase(Method[] methods, String fieldName) {
        for (Method m : methods) {
            if (m.isAnnotationPresent(ExchangeParameter.class)
                    && m.getName().equalsIgnoreCase("set" + fieldName)) {
                return m;
            }
        }
        return null;
    }

    // TODO move this method to an utils class!
    public static SubstrateNode getSubstrateNodeForId(int id) {
        for (SubstrateNode sn : ToolKit.getScenario().getNetworkStack()
                .getSubstrate().getVertices()) {
            if (sn.getId() == id) {
                return sn;
            }
        }
        throw new AssertionError("No Substrate node found with id " + id + ".");
    }

    // TODO move this method to an utils class!
    public static VirtualNode getVirtualNodeForId(int layer, int id) {
        for (Object vn : ToolKit.getScenario().getNetworkStack()
                .getLayer(layer).getVertices()) {
            if (((VirtualNode) vn).getId() == id) {
                return (VirtualNode) vn;
            }
        }
        throw new AssertionError("No virtual node found with id " + id + ".");
    }

    // TODO move to an utils class!
    public static VirtualNode getVirtualNodeForId(int id) {
        for (int layer = 1; ToolKit.getScenario().getNetworkStack()
                .getLayer(layer) != null; layer++)
            for (Object vn : ToolKit.getScenario().getNetworkStack()
                    .getLayer(layer).getVertices()) {
                if (((VirtualNode) vn).getId() == id) {
                    return (VirtualNode) vn;
                }
            }
        throw new AssertionError("No virtual node found with id " + id + ".");
    }

    /**
     * Returns the resource of the given type available at the substrate network
     * entity with the given id.
     *
     * @param substrateId The id of the substrate network entity.
     * @param resType     The resource type (class name).
     * @return The resource of the given type available at the given substrate
     * network entity
     */
    private AbstractResource getResource(int substrateId, String resType) {
        SubstrateNetwork substrate = ToolKit.getScenario().getNetworkStack()
                .getSubstrate();
        for (SubstrateNode node : substrate.getVertices()) {
            if (node.getId() == substrateId) {
                for (AbstractResource res : node.get())
                    if (res.getClass().getSimpleName().equals(resType))
                        return res;
            }
        }
        for (SubstrateLink link : substrate.getEdges()) {
            if (link.getId() == substrateId) {
                for (AbstractResource res : link.get())
                    if (res.getClass().getSimpleName().equals(resType))
                        return res;
            }
        }
        throw new AssertionError("No resource found matching substrate entity "
                + substrateId + " and type " + resType + ".");
    }
}
