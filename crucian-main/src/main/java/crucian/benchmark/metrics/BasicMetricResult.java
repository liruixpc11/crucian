package crucian.benchmark.metrics;

import java.util.Collections;
import java.util.Map;

/**
 * Created at 14-3-18 下午6:57.
 *
 * @author lirui
 */
public class BasicMetricResult implements MetricResult {
    private String key;
    private String name;
    private Number value;
    private Number maxValue;

    public BasicMetricResult(String key, Number value, Number maxValue) {
        this.name = this.key = key;
        this.value = value;
        this.maxValue = maxValue;
    }

    public BasicMetricResult(String key, String name, Number value) {
        this.key = key;
        this.name = name;
        this.maxValue = this.value = value;
    }

    public BasicMetricResult(String key, Number value) {
        this.key = this.name = key;
        this.maxValue = this.value = value;
    }

    public BasicMetricResult(String key, String name, Number value, Number maxValue) {
        this.key = key;
        this.name = name;
        this.value = value;
        this.maxValue = maxValue;
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    @Override
    public Number getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Number maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public Number getRate() {
        return value.doubleValue() / maxValue.doubleValue();
    }

    public Map<String, MetricResult> toMap() {
        return Collections.singletonMap(key, (MetricResult) this);
    }

    public static MetricResult single(Map<String, MetricResult> metricResultMap) {
        for (MetricResult metricResult : metricResultMap.values()) {
            return metricResult;
        }

        return null;
    }
}
