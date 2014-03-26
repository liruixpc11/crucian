package crucian.benchmark.simulator;

/**
 * Created at 14-3-24 下午12:56.
 *
 * @author lirui
 */
public interface SimulatorPlugin {
    /**
     * 模拟器开始事件循环时调用
     *
     * @param simulator 模拟器
     */
    public void initialize(Simulator simulator);

    /**
     * 模拟器结束了事件循环时调用
     *
     * @param simulator 模拟器
     */
    public void uninitialize(Simulator simulator);

    /**
     * 当接收到事件后立即调用，在{@code SimulatorListener }前调用
     *
     * @param simulator 模拟器
     * @param event 事件
     */
    public void onEventBegin(Simulator simulator, Event event);

    /**
     * 当事件处理完毕后调用，在{@code SimulatorListener}调用后调用
     *
     * @param simulator 模拟器
     * @param event 事件
     */
    public void onEventEnd(Simulator simulator, Event event);
}
