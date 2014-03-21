package crucian.test;


import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author LiRuiNet
 *         14-2-22 上午10:29
 */
public class DesktopTestAction extends AbstractAction {
    public DesktopTestAction() {
        super("MDI测试");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame((String) getValue(NAME));
        JDesktopPane desktopPane = new JDesktopPane();
        JInternalFrame[] internalFrames = {
                new JInternalFrame("Can Do All", true, true, true, true),
                new JInternalFrame("Not Resizable", false, true, true, true),
                new JInternalFrame("Not Closable", true, false, true, true),
                new JInternalFrame("Not Maximizable", true, true, false, true),
                new JInternalFrame("Not Iconifiable", true, true, true, false),
        };

        InternalFrameAdapter listener = new InternalFrameAdapter() {
            @Override
            public void internalFrameIconified(InternalFrameEvent e) {
                JInternalFrame source = (JInternalFrame) e.getSource();
                System.out.println("Iconified: " + source.getTitle());
            }

            @Override
            public void internalFrameDeiconified(InternalFrameEvent e) {
                JInternalFrame source = (JInternalFrame) e.getSource();
                System.out.println("Deiconified: " + source.getTitle());
            }
        };

        int pos = 0;
        for (JInternalFrame internalFrame : internalFrames) {
            desktopPane.add(internalFrame);

            internalFrame.setBounds(pos * 25, pos * 25, 200, 100);
            pos++;

            internalFrame.addInternalFrameListener(listener);

            JLabel label = new JLabel(internalFrame.getTitle(), JLabel.CENTER);
            internalFrame.add(label, BorderLayout.CENTER);

            internalFrame.setVisible(true);
        }

        JInternalFrame palette = new JInternalFrame("Palette", true, false, true, false);
        palette.setBounds(350, 150, 100, 100);
        palette.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
        desktopPane.add(palette, BorderLayout.CENTER);
        palette.setVisible(true);
        // desktopPane.setDragMode();
        frame.add(desktopPane, BorderLayout.CENTER);
        frame.setSize(500, 300);
        frame.setVisible(true);
    }
}
