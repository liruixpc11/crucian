package crucian.benchmark;

import mulavito.algorithms.AbstractAlgorithmStatus;

import java.util.List;

/**
 * @author LiRuiNet
 *         14-2-28 下午3:30
 */
public interface StatusReporter {
    public void report(List<AbstractAlgorithmStatus> statuses);

    public void report(Exception ex);
}
