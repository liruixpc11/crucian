package crucian.benchmark.simulator;

/**
 * Created at 14-3-3 上午11:29.
 *
 * @author lirui
 */
public interface SimulatorListener {
    /**
     * 当事件队列为空时调用
     *
     * @param simulator 模拟器
     * @return 是否补充了新的事件
     */
    public boolean onEmpty(Simulator simulator);

    /**
     * 当成功处理事件之后
     *
     * @param simulator 模拟器
     * @param event     处理的事件
     */
    void onSuccess(Simulator simulator, Event event);

    /**
     * 当处理事件失败之后
     *
     * @param simulator 模拟器
     * @param event     处理的事件
     * @param cause     出错原因
     */
    void onError(Simulator simulator, Event event, Throwable cause);

    /**
     * 映射前调用
     *
     * @param simulator 模拟器
     * @param event     处理的事件
     */
    void preMap(Simulator simulator, Event event);
}
