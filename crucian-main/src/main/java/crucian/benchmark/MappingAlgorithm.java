package crucian.benchmark;

import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.virtual.VirtualNetwork;

/**
 * @author LiRuiNet
 *         14-2-28 上午9:47
 */
public interface MappingAlgorithm {
    /**
     * 初始化，例如初始化底层网络资源的统计信息，以便后续增量更新
     *
     * @param substrateNetwork 底层网络
     */
    public void initialize(SubstrateNetwork substrateNetwork);

    /**
     * 映射
     *
     * @param substrateNetwork 底层网络
     * @param virtualNetwork   虚拟网络
     * @throws Exception
     */
    public void map(SubstrateNetwork substrateNetwork, VirtualNetwork virtualNetwork) throws Exception;
}
