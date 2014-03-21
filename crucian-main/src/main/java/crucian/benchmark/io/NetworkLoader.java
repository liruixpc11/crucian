package crucian.benchmark.io;

/**
 * Created at 14-3-4 上午9:39.
 *
 * @author lirui
 */
public interface NetworkLoader<N> {
    public N load(Object source) throws Exception;
}
