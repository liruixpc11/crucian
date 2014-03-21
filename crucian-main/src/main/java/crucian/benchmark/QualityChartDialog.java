package crucian.benchmark;

import mulavito.algorithms.AbstractAlgorithmStatus;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;

/**
 * Created at 14-3-17 下午10:14.
 *
 * @author lirui
 */
public class QualityChartDialog extends JDialog implements StatusDisplayable {
    private ITimeStatsProvider statsProvider;
    private String title;
    private final ChartDescription[] chartDescriptions = new ChartDescription[]{
            new ChartDescription("请求接受率", QualityMetrics.acceptRate),
            new ChartDescription("底层网络开销", QualityMetrics.substrateCost),
            new ChartDescription("平均时间开销", QualityMetrics.avgTime)
    };

    private static class ChartDescription {
        String title;
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String type;

        private ChartDescription(String title, String type) {
            this.title = title;
            this.type = type;
        }
    }

    public QualityChartDialog(Frame owner, String title, ITimeStatsProvider statsProvider) {
        super(owner, title);
        this.title = title;
        this.statsProvider = statsProvider;

        int rows = 0;
        int columns = (int) Math.sqrt(chartDescriptions.length);
        if (columns > 1) {
            rows = (int) Math.ceil(chartDescriptions.length / (double) columns);
        }

        setLayout(new GridLayout(rows, columns));

        for (ChartDescription chartDescription : chartDescriptions) {
            JFreeChart lineChart = ChartFactory.createLineChart(
                    chartDescription.title,
                    "时间单元",
                    chartDescription.title,
                    chartDescription.dataset,
                    PlotOrientation.VERTICAL,
                    false,
                    true,
                    false
            );

            add(new ChartPanel(lineChart));
        }

        setSize(1200, 730);

        updateStats();
    }

    @Override
    public void setAlgorithmName(String algorithmName) {
        setTitle(algorithmName + title);
    }

    public void updateStats() {
        for (ChartDescription chartDescription : chartDescriptions) {
            addData(chartDescription.type, chartDescription.dataset);
        }
    }

    private void addData(String type, DefaultCategoryDataset dataset) {
        AbstractAlgorithmStatus algorithmStatus = statsProvider.getStatus(type);
        if (algorithmStatus != null) {
            dataset.addValue(algorithmStatus.getValue(), type, statsProvider.getTime());
        }
    }

    public void reset() {
    }
}
