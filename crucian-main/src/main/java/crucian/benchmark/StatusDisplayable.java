package crucian.benchmark;

/**
 * Created at 14-3-18 上午9:58.
 *
 * @author lirui
 */
public interface StatusDisplayable {
    public void setAlgorithmName(String algorithmName);

    public void updateStats();

    public void reset();

    public void setVisible(boolean visible);
}
