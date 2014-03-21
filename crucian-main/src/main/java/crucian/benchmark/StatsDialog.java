package crucian.benchmark;

import mulavito.algorithms.AbstractAlgorithmStatus;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created at 14-3-4 下午12:52.
 *
 * @author lirui
 */
public class StatsDialog extends JDialog implements StatusDisplayable {
    private IStatsProvider statsProvider;
    private Map<String, UiEntry> uiEntryMap = new HashMap<String, UiEntry>();
    private String title;

    private static class UiEntry {
        JLabel label;
        JLabel content;

        private UiEntry(JLabel label, JLabel content) {
            this.label = label;
            this.content = content;
        }
    }

    public StatsDialog(Frame owner, IStatsProvider statsProvider, String title) {
        super(owner);

        this.statsProvider = statsProvider;

        this.title = title;
        setTitle(title);
        setSize(400, 300);
        setLayout(new GridLayout(0, 2));
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(HIDE_ON_CLOSE);

        updateStats();
    }

    @Override
    public void setAlgorithmName(String algorithmName) {
        setTitle(algorithmName + title);
    }

    public void updateStats() {
        Set<String> labels = new HashSet<String>();
        for (AbstractAlgorithmStatus status : statsProvider.getStats()) {
            String label = status.getLabel();
            String content = String.format("%f/%f(%d%%)",
                    status.getValue().doubleValue(),
                    status.getMaximum().doubleValue(),
                    status.getRatio());
            labels.add(label);
            UiEntry uiEntry = uiEntryMap.get(label);
            if (uiEntry == null) {
                uiEntry = new UiEntry(new JLabel(label), new JLabel(content));
                add(uiEntry.label);
                add(uiEntry.content);
                uiEntryMap.put(label, uiEntry);
            } else {
                uiEntry.content.setText(content);
            }
        }

        Set<String> removedLabels = new HashSet<String>(uiEntryMap.keySet());
        removedLabels.removeAll(labels);
        for (String removedLabel : removedLabels) {
            UiEntry uiEntry = uiEntryMap.remove(removedLabel);
            remove(uiEntry.label);
            remove(uiEntry.content);
        }

        pack();
    }

    @Override
    public void reset() {
    }
}
