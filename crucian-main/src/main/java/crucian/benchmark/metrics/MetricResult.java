package crucian.benchmark.metrics;

/**
 * Created at 14-3-18 下午9:45.
 *
 * @author lirui
 */
public interface MetricResult {
    String getKey();

    String getName();

    Number getValue();

    Number getMaxValue();

    Number getRate();
}
