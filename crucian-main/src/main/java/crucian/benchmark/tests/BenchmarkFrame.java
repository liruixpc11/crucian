package crucian.benchmark.tests;

import crucian.gui.menu.BenchmarkMenu;

import javax.swing.*;
import java.awt.*;

/**
 * Created at 14-3-18 上午10:36.
 *
 * @author lirui
 */
public class BenchmarkFrame {
    public static void main(String[] args) {
        JFrame frame = new JFrame("基准测试");
        frame.setJMenuBar(new JMenuBar());
        frame.getJMenuBar().add(new BenchmarkMenu());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - frame.getWidth()) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - frame.getHeight()) / 2;
        frame.setLocation(x, y);
        frame.setVisible(true);
    }
}
