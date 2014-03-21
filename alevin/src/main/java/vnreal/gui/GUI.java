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
package vnreal.gui;

import mulavito.gui.Gui;
import mulavito.gui.components.selectionpanel.SelectionPanel;
import mulavito.utils.Resources;
import vnreal.ToolKit;
import vnreal.gui.mapping.MappingPanel;
import vnreal.gui.menu.*;
import vnreal.gui.utils.MySelectionTreeModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * The main GUI class.
 *
 * @author Vlad Singeorzan
 * @author Michael Duelli
 * @since 2010-10-04
 */
@SuppressWarnings("serial")
public class GUI extends Gui {
    /**
     * To have a graph panel which accepts substrate and virtual networks, we
     * intentionally omit generics here.
     */
    @SuppressWarnings("unchecked")
    private MyGraphPanel graphpanel;
    private ToolBar toolbar;
    private MappingPanel mappingPanel;
    private static GUI singleton;

    public GUI() {
        this("ALEVIN - ALgorithms for Embedding VIrtual Networks");
    }

    public GUI(String title) {
        this(title, new JMenu[0]);
    }

    public GUI(String title, JMenu[] menus) {
        super(title);
        singleton = this;
        // setPreferredSize(preferredSize)

        // Set the frame icon.
        ImageIcon icon = Resources.getIconByName("/img/alevin-logo.png");
        if (icon != null)
            setIconImage(icon.getImage());

        toolbar = new ToolBar(graphpanel);
        // Add tool bar.
        getToolBarPane().add(toolbar);

        // Add menu bar.
        JMenuBar menubar = new JMenuBar();
        menubar.add(new FileMenu());
        menubar.add(new ViewMenu());
        menubar.add(new GeneratorMenu());
        menubar.add(new AlgorithmsMenu());
        menubar.add(new MetricsMenu());
        for (JMenu menu : menus) {
            menubar.add(menu);
        }
        menubar.add(Box.createHorizontalGlue());
        menubar.add(new HelpMenu());
        setJMenuBar(menubar);

        normalOutput("Welcome to ALEVIN.\n");
        // debugOutput("Click \"Import\" to create an exemplary scenario.\n");
        // warnOutput("Have fun!!!\n");

        ToolKit.getScenario().addChangeListener(new ChangeListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void stateChanged(ChangeEvent e) {
                // Network stack was updated.
                graphpanel.setLayerStack(ToolKit.getScenario()
                        .getNetworkStack());
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
                dispose();

                System.exit(0);
            }
        });

        pack();
        setVisible(true);
    }

    public ToolBar getToolBar() {
        return toolbar;
    }

    @Override
    protected JComponent createRightPane() {
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Selection", new SelectionPanel(new MySelectionTreeModel(),
                graphpanel));
        tabs.addTab("Mapping", mappingPanel = new MappingPanel(graphpanel));

        tabs.setPreferredSize(new Dimension(250, 300));
        return tabs;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected JComponent createCenterPane() {
        graphpanel = new MyGraphPanel();
        graphpanel.setPreferredSize(new Dimension(520, 300));
        graphpanel.setSynced(false);
        graphpanel.setLayerStack(ToolKit.getScenario().getNetworkStack());

        graphpanel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("Viewers")) {
                    if (evt.getNewValue() != null) {
                        getStatusManager()
                                .addHintFor(
                                        (Component) evt.getNewValue(),
                                        "Hint: CTRL+Mouse Wheel for Zoom, "
                                                + "SHIFT+Left click to select arbitrary elements");
                        getStatusManager().enableHints(
                                (Component) evt.getNewValue(), true);
                    } else if (evt.getOldValue() != null) {
                        getStatusManager().removeHint(
                                (Component) evt.getOldValue());
                        getStatusManager().enableHints(
                                (Component) evt.getOldValue(), false);
                    }
                }
            }
        });

        return graphpanel;
    }

    public MyGraphPanel<?, ?, ?, ?> getGraphPanel() {
        return graphpanel;
    }

    public void update() {
        mappingPanel.update();
    }

    public static GUI getInstance() {
        return singleton;
    }

    public static boolean isInitialized() {
        return (singleton != null);
    }
}
