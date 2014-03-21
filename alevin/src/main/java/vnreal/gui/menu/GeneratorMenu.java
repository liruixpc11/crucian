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

import vnreal.ToolKit;
import vnreal.gui.GUI;
import vnreal.gui.dialog.ConstraintsGeneratorDialog;
import vnreal.gui.dialog.ScenarioWizard;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * @author Michael Duelli
 * @since 2010-12-08
 */
@SuppressWarnings("serial")
public final class GeneratorMenu extends JMenu implements ActionListener {
    public GeneratorMenu() {
        super("Generators");
        setMnemonic(KeyEvent.VK_G);

        JMenuItem mi;

        mi = new JMenuItem("Scenario Wizard");
        mi.setActionCommand("scenario wizard");
        mi.addActionListener(this);
        add(mi);

        addSeparator();

        mi = new JMenuItem("Generate Constraints");
        mi.setActionCommand("generate constraints");
        mi.addActionListener(this);
        add(mi);

        addSeparator();

        mi = new JMenuItem("Clear all constraints");
        mi.setActionCommand("clear");
        mi.addActionListener(this);
        add(mi);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if (cmd.equals("clear")) {
            ToolKit.getScenario().getNetworkStack().clearConstraints();
            GUI.getInstance().notifyOutput("Cleared all constraints.\n");
        } else if (cmd.equals("scenario wizard")) {
            new ScenarioWizard();
        } else if (cmd.equals("generate constraints")) {
            // only with an open scenario
            if (ToolKit.getScenario().getNetworkStack() == null) {
                JOptionPane
                        .showMessageDialog(
                                GUI.getInstance(),
                                "Error: A Scenario must be open to generate Constraints.",
                                "Generate Constraints",
                                JOptionPane.ERROR_MESSAGE);
            } else {
                new ConstraintsGeneratorDialog();
            }
        }
    }
}
