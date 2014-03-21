package crucian.benchmark;

import mulavito.algorithms.AbstractAlgorithmStatus;
import vnreal.network.NetworkStack;

import java.util.List;

/**
 * @author LiRuiNet
 *         14-2-28 上午9:45
 */
public interface Benchmark {
    /**
     * 基准测试名称
     *
     * @return 基准测试名称
     */
    public String getName();

    /**
     * 设置测试场景
     *
     * @param networks 测试场景
     * @return 自身，支持流式接口调用
     */
    public Benchmark setScenario(NetworkStack networks);

    /**
     * 评测映射算法，结果通过接口{@code Benchmark.getStatus()}获取
     *
     * @param mappingAlgorithm 映射算法
     * @return 自身，支持流式接口调用
     */
    public Benchmark evaluate(MappingAlgorithm mappingAlgorithm);

    /**
     * 获取评测结果
     *
     * @return 评测结果
     */
    public List<AbstractAlgorithmStatus> getStatus();

    /**
     * 报告评测结果
     *
     * @param reporter 评测报告器
     * @return 自身，支持流式接口调用
     */
    public Benchmark reportStatus(StatusReporter reporter);

    /**
     * 恢复初始状态，包括清除所有统计信息、映射关系
     *
     * @return 自身，支持流式接口调用
     */
    public Benchmark clear();

    /**
     * 是否需要给定网络栈
     *
     * @return 是否
     */
    public boolean isNeedScenario();
}
