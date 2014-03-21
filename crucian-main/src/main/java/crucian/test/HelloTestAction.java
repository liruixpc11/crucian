package crucian.test;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author LiRuiNet
 *         14-2-20 下午7:01
 */
public class HelloTestAction extends AbstractAction {
    public HelloTestAction() {
        super("你好");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(null, "你好", "hello, world", JOptionPane.PLAIN_MESSAGE);
    }
}
