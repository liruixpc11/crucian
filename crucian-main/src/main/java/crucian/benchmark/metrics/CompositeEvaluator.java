package crucian.benchmark.metrics;

import java.util.*;

/**
 * Created at 14-3-18 下午10:12.
 *
 * @author lirui
 */
public class CompositeEvaluator<T> implements MetricEvaluator<T> {
    private List<MetricEvaluator<T>> metricEvaluators = new ArrayList<>();

    @SafeVarargs
    public CompositeEvaluator(MetricEvaluator<T>... evaluators) {
        metricEvaluators.addAll(Arrays.asList(evaluators));
    }

    @SafeVarargs
    public CompositeEvaluator(Class<? extends MetricEvaluator<T>>... evaluatorClasses) {
        for (Class<? extends MetricEvaluator<T>> evaluatorClass : evaluatorClasses) {
            try {
                metricEvaluators.add(evaluatorClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Map<String, MetricResult> evaluate(T t) {
        Map<String, MetricResult> metricResultMap = new HashMap<>();
        for (MetricEvaluator<T> evaluator : metricEvaluators) {
            Map<String, MetricResult> result = evaluator.evaluate(t);
            metricResultMap.putAll(result);
        }

        return metricResultMap;
    }
}
