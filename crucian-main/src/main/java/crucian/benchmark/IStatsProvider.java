package crucian.benchmark;

import mulavito.algorithms.AbstractAlgorithmStatus;

import java.util.List;

/**
 * Created at 14-3-4 下午1:00.
 *
 * @author lirui
 */
public interface IStatsProvider {
    public List<AbstractAlgorithmStatus> getStats();

    public AbstractAlgorithmStatus getStatus(String key);
}
