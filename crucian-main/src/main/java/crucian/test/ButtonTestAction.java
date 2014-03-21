package crucian.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author LiRuiNet
 *         14-2-20 下午11:16
 */
public class ButtonTestAction extends AbstractAction {
    public ButtonTestAction() {
        super("Button示例");
    }

    public static class ButtonPanel extends JPanel {
        private ButtonGroup group = new ButtonGroup();

        public ButtonPanel() {
            super(new GridLayout(0, 1));
            setBorder(BorderFactory.createTitledBorder("Button Examples"));
            addButton(new JToggleButton("Toggle Button"));
            addButton(new JRadioButton("Radio Button"));
            addButton(new JCheckBox("Check Box"));
            addButton(new JRadioButtonMenuItem("Radio Menu Item"));
            addButton(new JCheckBoxMenuItem("Check Box Menu Item"));
        }

        private void addButton(AbstractButton button) {
            group.add(button);
            add(button);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFrame frame = new JFrame("Button Demo");
        frame.add(new ButtonPanel(), BorderLayout.CENTER);
        frame.setSize(300, 300);
        frame.setVisible(true);
    }
}
