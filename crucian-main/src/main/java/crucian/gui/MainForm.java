package crucian.gui;

import crucian.gui.menu.BenchmarkMenu;
import crucian.gui.menu.TestMenu;
import vnreal.gui.GUI;

import javax.swing.*;

/**
 * Created at 14-3-21 上午9:19.
 *
 * @author lirui
 */
public class MainForm extends GUI {
    public MainForm() {
        this("crucian - Virtual Network Embedding Simulator");
    }

    public MainForm(String title) {
        super(title, new JMenu[]{
                new BenchmarkMenu(),
                new TestMenu()
        });
    }
}
