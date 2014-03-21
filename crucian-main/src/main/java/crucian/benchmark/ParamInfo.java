package crucian.benchmark;

/**
 * @author LiRuiNet
 *         14-2-28 下午5:55
 */
public class ParamInfo {
    private String name;
    private Class<?> type;
    private String defaultValue;

    public ParamInfo(String name, Class<?> type) {
        this.name = name;
        this.type = type;
    }

    public ParamInfo(String name, Class<?> type, String defaultValue) {
        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
