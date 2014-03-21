package crucian.benchmark;

import mulavito.algorithms.AbstractAlgorithmStatus;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @author LiRuiNet
 *         14-2-28 下午3:51
 */
public class ConsoleReporter implements StatusReporter {
    @Override
    public void report(List<AbstractAlgorithmStatus> statuses) {
        System.out.println("=================================================================================");
        for (AbstractAlgorithmStatus status : statuses) {
            System.out.println(String.format("%s:\t%s", status.getLabel(), new DecimalFormat(",###").format(status.getValue())));
        }
    }

    @Override
    public void report(Exception ex) {
        System.out.println("==== ERROR ======================================================================");
        ex.printStackTrace();
    }
}
