package crucian.benchmark.metrics;

import java.util.Map;

/**
 * 指标评价器
 * Created at 14-3-18 下午7:01.
 *
 * @author lirui
 */
public interface MetricEvaluator<T> {
    /**
     * 评价
     *
     * @param t 待评价客体
     * @return 评价结果
     */
    public Map<String, MetricResult> evaluate(T t);
}
