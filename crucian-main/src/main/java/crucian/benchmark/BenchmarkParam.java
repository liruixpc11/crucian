package crucian.benchmark;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author LiRuiNet
 *         14-2-28 下午4:22
 */
@Target({
        ElementType.PARAMETER
})
@Retention(RetentionPolicy.RUNTIME)
public @interface BenchmarkParam {
    public String value();
}
