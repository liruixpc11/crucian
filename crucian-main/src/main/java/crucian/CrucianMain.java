package crucian;

import crucian.gui.MainForm;

import java.awt.*;

/**
 * Created at 14-3-21 上午9:25.
 *
 * @author lirui
 */
public class CrucianMain {
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainForm();
            }
        });
    }
}
