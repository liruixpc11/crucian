package crucian.test;

import vnreal.gui.GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author LiRuiNet
 *         14-2-22 下午6:46
 */
public class ProgressMonitorTestAction extends AbstractAction {
    public ProgressMonitorTestAction() {
        super("进度条");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final ProgressMonitor progressMonitor = new ProgressMonitor(GUI.getInstance(), "Demo", "1", 0, 100);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int progress = 0;
                while (progress < 100 && !progressMonitor.isCanceled()) {
                    progressMonitor.setNote(String.format("progress: %d", progress));
                    progressMonitor.setProgress(progress);
                    progress++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
