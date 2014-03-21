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
package vnreal.gui.menu;

import mulavito.gui.dialogs.LocatableFileChooser;
import mulavito.utils.Resources;
import vnreal.ToolKit;
import vnreal.gui.GUI;
import vnreal.gui.control.MyFileChooser;
import vnreal.gui.control.MyFileChooser.MyFileChooserType;
import vnreal.gui.utils.FileFilters;
import vnreal.io.ScenarioExporter;
import vnreal.io.ScenarioImporter;
import vnreal.network.NetworkStack;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

/**
 * @author Vlad Singeorzan
 * @author Michael Duelli
 * @since 2010-10-19
 */
@SuppressWarnings("serial")
public final class FileMenu extends JMenu implements ActionListener {
    /**
     * The default modifier for keyboard shortcuts
     */
    protected static final int modifier = Toolkit.getDefaultToolkit()
            .getMenuShortcutKeyMask();

    public FileMenu() {
        super("File");
        setMnemonic(KeyEvent.VK_F);

        JMenuItem empty = new JMenuItem("New empty scenario / layers");
        empty.setActionCommand("empty_scenario");
        empty.addActionListener(this);
        empty.setIcon(Resources.getIconByName("/places/network-workgroup.png"));
        empty.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, modifier));
        add(empty);

        addSeparator();

        JMenuItem imp = new JMenuItem("Import");
        imp.setActionCommand("import");
        imp.setIcon(Resources.getIconByName("/actions/go-last.png"));
        imp.addActionListener(this);
        imp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, modifier));
        add(imp);
        JMenuItem exp = new JMenuItem("Export");
        exp.setActionCommand("export");
        exp.setIcon(Resources.getIconByName("/actions/go-first.png"));
        exp.addActionListener(this);
        exp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, modifier));
        add(exp);

        addSeparator();

        JMenuItem close = new JMenuItem("Close");
        close.setActionCommand("close");
        close.addActionListener(this);
        close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, modifier));
        add(close);

        addSeparator();

        JMenuItem quit = new JMenuItem("Quit");
        quit.setActionCommand("quit");
        quit.setIcon(Resources.getIconByName("/actions/system-log-out.png"));
        quit.addActionListener(this);
        quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, modifier));
        add(quit);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("import")) {
            // import a scenario from XML
            MyFileChooser fileChooser = new MyFileChooser("Scenario Import",
                    MyFileChooserType.XML, true);
            fileChooser.addChoosableFileFilter(FileFilters.xmlFilter);
            fileChooser.showOpenDialog(GUI.getInstance());
            if (fileChooser.getSelectedFile() != null) {
                try {
                    new ScenarioImporter(fileChooser.getSelectedFile()
                            .getCanonicalPath()).setNetworkStack();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else
                System.err.println("User aborted import selection.");
        } else if (cmd.equals("export")) {
            // export the current scenario to XML
            if (ToolKit.getScenario().getNetworkStack() != null) {
                LocatableFileChooser fileChooser = new LocatableFileChooser(
                        "Scenario Export", 0, false);
                fileChooser.addChoosableFileFilter(FileFilters.xmlFilter);
                fileChooser.showSaveDialog(this);
                if (fileChooser.getSelectedFile() != null) {
                    try {
                        String fileName = fileChooser.getSelectedFile()
                                .getAbsolutePath();

                        if (!fileName.endsWith(".xml"))
                            fileName = fileName + ".xml";

                        new ScenarioExporter(fileName).write();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else
                JOptionPane.showMessageDialog(GUI.getInstance(),
                        "No scenario opened, no export possible.");
        } else if (cmd.equals("empty_scenario")) {
            // create a new scenario or add empty layers to an existing one
            String numVNs = JOptionPane.showInputDialog(GUI.getInstance(),
                    "Number of virtual networks:");
            if (numVNs != null) {
                try {
                    NetworkStack stack = ToolKit.getScenario()
                            .getNetworkStack();
                    if (ToolKit.getScenario().getNetworkStack() == null) {
                        // Create new empty network stack.
                        ToolKit.getScenario().setNetworkStack(
                                new NetworkStack(new SubstrateNetwork(),
                                        new LinkedList<VirtualNetwork>()));
                        stack = ToolKit.getScenario().getNetworkStack();
                    }

                    // Add numVNs additional layers.
                    int layers = stack.size();
                    for (int i = 0; i < Integer.parseInt(numVNs); i++)
                        stack.addLayer(new VirtualNetwork(layers + i));
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(GUI.getInstance(),
                            "Invalid number of virtual networks.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        } else if (cmd.equals("close")) {
            // close the scenario
            if (JOptionPane.showConfirmDialog(GUI.getInstance(),
                    "Do you really want to close the current scenarion?",
                    "Close Scenario", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                ToolKit.getScenario().setNetworkStack(null);
            }
        } else if (cmd.equals("quit")) {
            // quit the application
            if (JOptionPane
                    .showConfirmDialog(
                            GUI.getInstance(),
                            "Do you really want to close the current scenario and quit ALEVIN?",
                            "Quit", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                System.exit(0);
            }
        }
    }
}
