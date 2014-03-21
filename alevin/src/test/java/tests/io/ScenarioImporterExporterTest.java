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
package tests.io;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import vnreal.io.ScenarioExporter;
import vnreal.io.ScenarioImporter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Locale;

/**
 * @author Vlad Singeorzan
 */
@Ignore
public final class ScenarioImporterExporterTest {

    @Before
    public void setLocale() {
        Locale.setDefault(Locale.US);
    }

    @Test
    public void importExportImportAgainTest() {
        ScenarioImporter importer = new ScenarioImporter(
                getClass().getClassLoader().getResourceAsStream("XML/exemplary-scenario-mappings-and-hiddenhops.xml"));
        importer.setNetworkStack();

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ScenarioExporter exporter = new ScenarioExporter(os);
        if (exporter.init()) {
            exporter.write();
        }
        byte[] bytes = os.toByteArray();
        ScenarioImporter importer2 = new ScenarioImporter(
                new ByteArrayInputStream(bytes));
        importer2.setNetworkStack();
    }

    @Test
    public void importExportImportAgainTestWithMappings() {
        ScenarioImporter importer = new ScenarioImporter(
                getClass().getClassLoader().getResourceAsStream("XML/exemplary-scenario-mappings-and-hiddenhops.xml"));
        importer.setNetworkStack();

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ScenarioExporter exporter = new ScenarioExporter(os);
        if (exporter.init()) {
            exporter.write();
        }
        byte[] bytes = os.toByteArray();
        ScenarioImporter importer2 = new ScenarioImporter(
                new ByteArrayInputStream(bytes));
        importer2.setNetworkStack();
    }

    @Test
    public void importExportImportAgainTestWithMappingsAndHiddenHops() {
        ScenarioImporter importer = new ScenarioImporter(
                getClass().getClassLoader().getResourceAsStream("XML/exemplary-scenario-mappings-and-hiddenhops.xml"));
        importer.setNetworkStack();

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ScenarioExporter exporter = new ScenarioExporter(os);
        if (exporter.init()) {
            exporter.write();
        }
        byte[] bytes = os.toByteArray();
        ScenarioImporter importer2 = new ScenarioImporter(new ByteArrayInputStream(bytes));
        importer2.setNetworkStack();
    }
}
