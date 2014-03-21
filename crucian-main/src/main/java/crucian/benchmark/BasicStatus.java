package crucian.benchmark;

import mulavito.algorithms.AbstractAlgorithmStatus;

/**
 * @author LiRuiNet
 *         14-2-28 下午2:32
 */
public class BasicStatus extends AbstractAlgorithmStatus {
    private String key;
    private Number value;
    private Number maxValue;

    public BasicStatus(String label, Number value) {
        super(label);
        this.maxValue = this.value = value;
        this.key = label;
    }

    public BasicStatus(String label, String key, Number value) {
        super(label);
        this.key = key;
        this.maxValue = this.value = value;
    }

    public BasicStatus(String label, String key, Number value, Number maxValue) {
        super(label);
        this.key = key;
        this.value = value;
        this.maxValue = maxValue;
    }

    public BasicStatus(String label, Number value, Number maxValue) {
        this(label, value);
        this.maxValue = maxValue;
        this.key = label;
    }

    @Override
    public Number getValue() {
        return value;
    }

    @Override
    public Number getMaximum() {
        return maxValue;
    }
}
