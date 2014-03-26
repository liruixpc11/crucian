package crucian.algorithms.coarsening;

import crucian.benchmark.MappingAlgorithm;
import crucian.benchmark.algorithms.IsomorphismAlgorithm;
import crucian.benchmark.generators.RandomGtItmSubstrateNetworkGenerator;
import crucian.benchmark.generators.RandomGtItmVirtualNetworkGenerator;
import crucian.benchmark.metrics.SubstrateCostEvaluator;
import org.apache.commons.collections15.Transformer;
import vnreal.algorithms.utils.MiscelFunctions;
import vnreal.demands.AbstractDemand;
import vnreal.demands.BandwidthDemand;
import vnreal.demands.CpuDemand;
import vnreal.mapping.Mapping;
import vnreal.network.NetworkEntity;
import vnreal.network.substrate.SubstrateLink;
import vnreal.network.substrate.SubstrateNetwork;
import vnreal.network.substrate.SubstrateNode;
import vnreal.network.virtual.VirtualLink;
import vnreal.network.virtual.VirtualNetwork;
import vnreal.network.virtual.VirtualNode;
import vnreal.resources.AbstractResource;
import vnreal.resources.BandwidthResource;
import vnreal.resources.CpuResource;

import java.util.*;

public class BasicCoarsening implements VirtualNetworkCoarsening {
    private boolean useLog = false;
    private boolean supportCluster = false;
    private boolean useAverageThreshold = false;

    // performance counters
    private long startTime;
    private long copyTime;
    private long metricBandwidthTime;
    private long snodeScanTime;
    private long metricTime;
    private long sortTime;
    private long combineTime;

    public long getStartTime() {
        return startTime;
    }

    public long getCopyTime() {
        return copyTime;
    }

    public long getMetricBandwidthTime() {
        return metricBandwidthTime;
    }

    public long getSnodeScanTime() {
        return snodeScanTime;
    }

    public long getMetricTime() {
        return metricTime;
    }

    public long getSortTime() {
        return sortTime;
    }

    public long getCombineTime() {
        return combineTime;
    }

    public boolean isUseAverageThreshold() {
        return useAverageThreshold;
    }

    public void setUseAverageThreshold(boolean useAverageThreshold) {
        this.useAverageThreshold = useAverageThreshold;
    }

    private static class VirtualNodePair {
        VirtualNode node1;
        VirtualNode node2;

        private VirtualNodePair(VirtualNode node1, VirtualNode node2) {
            this.node1 = node1;
            this.node2 = node2;
        }
    }

    private class CoarsenMetric {
        double maxBandwidth = 0;
        double maxCpu = 0;
        final VirtualNetwork coarsenedVirtualNetwork;
        final VirtualNetwork originalVirtualNetwork;
        final SubstrateNetwork substrateNetwork;

        Map<VirtualNode, Double> adjacentBandwidthMap = new HashMap<>();

        /**
         * CPU资源阈值，默认设为无限大
         */
        double thresholdCpu = Double.MAX_VALUE;

        /**
         * 带宽资源阈值，默认设为无限大
         */
        double thresholdBandwidth = Double.MAX_VALUE;

        Map<VirtualNode, CoarsenVirtualNode> coarsenMap = new HashMap<>();

        CoarsenMetric(VirtualNetwork coarsenedVirtualNetwork, VirtualNetwork originalVirtualNetwork, SubstrateNetwork substrateNetwork) {
            this.coarsenedVirtualNetwork = coarsenedVirtualNetwork;
            this.originalVirtualNetwork = originalVirtualNetwork;
            this.substrateNetwork = substrateNetwork;

            updateAdjacentBandwidthMap();
            metricBandwidthTime = System.nanoTime();
            updateThresholds();

            // log(String.format("cpu threshold: %f", thresholdCpu));
            // log(String.format("bandwidth threshold: %f", thresholdBandwidth));
        }

        public boolean canCombine(VirtualNode node1, VirtualNode node2) {
            VirtualLink virtualLink = findEdge(coarsenedVirtualNetwork, node1, node2);
            if (adjacentBandwidthMap.get(node1) + adjacentBandwidthMap.get(node2) - 2 * queryBandwidth(virtualLink) > thresholdBandwidth) {
                log("not enough bandwidth");
                return false;
            }

            if (queryCpu(node1) > thresholdCpu) {
                log("not enough cpu 1");
                return false;
            }

            if (queryCpu(node2) > thresholdCpu) {
                log("not enough cpu 2");
                return false;
            }

            return true;
        }

        private VirtualLink findEdge(VirtualNetwork virtualNetwork, VirtualNode node1, VirtualNode node2) {
            for (VirtualLink virtualLink : virtualNetwork.getIncidentEdges(node1)) {
                if (virtualNetwork.getSource(virtualLink) == node2 || virtualNetwork.getDest(virtualLink) == node2) {
                    return virtualLink;
                }
            }

            return null;
        }

        private void updateThresholds() {
            double[] cpuList = new double[substrateNetwork.getVertexCount()];
            double[] bandwidthList = new double[substrateNetwork.getVertexCount()];
            int index = 0;

            /*
            for (SubstrateNode substrateNode : substrateNetwork.getVertices()) {
                double cpu = queryCpu(substrateNode);
                double adjacentBandwidth = 0;
                for (SubstrateLink substrateLink : substrateNetwork.getIncidentEdges(substrateNode)) {
                    adjacentBandwidth += queryBandwidth(substrateLink);
                }

                cpuList[index] = cpu;
                bandwidthList[index] = adjacentBandwidth;
                index++;
            }
            */

            for (SubstrateNode substrateNode : substrateNetwork.getVertices()) {
                cpuList[index] = queryCpu(substrateNode);
                substrateNode.setTag(index);
                index++;
            }

            for (SubstrateLink substrateLink : substrateNetwork.getEdges()) {
                double bandwidth = queryBandwidth(substrateLink);
                bandwidthList[substrateNetwork.getSource(substrateLink).getTag()] += bandwidth;
                bandwidthList[substrateNetwork.getDest(substrateLink).getTag()] += bandwidth;
            }

            snodeScanTime = System.nanoTime();

            Arrays.sort(cpuList);
            Arrays.sort(bandwidthList);
            if (useAverageThreshold) {
                thresholdCpu = averageList(cpuList);
                thresholdBandwidth = averageList(bandwidthList);
            } else {
                thresholdCpu = cpuList[cpuList.length / 2];
                thresholdBandwidth = bandwidthList[bandwidthList.length / 2];
            }
            maxCpu = cpuList[cpuList.length - 1];
            maxBandwidth = bandwidthList[bandwidthList.length - 1];
        }

        private double averageList(double[] list) {
            double total = 0;
            for (double d : list) {
                total += d;
            }

            return total / list.length;
        }

        private void updateAdjacentBandwidthMap() {
            for (VirtualNode virtualNode : coarsenedVirtualNetwork.getVertices()) {
                updateAdjacentBandwidthMap(virtualNode);
            }
        }

        private void updateAdjacentBandwidthMap(VirtualNode virtualNode) {
            double bandwidth = 0;
            for (VirtualLink virtualLink : coarsenedVirtualNetwork.getIncidentEdges(virtualNode)) {
                bandwidth += queryBandwidth(virtualLink);
            }

            adjacentBandwidthMap.put(virtualNode, bandwidth);
        }
    }

    public boolean isSupportCluster() {
        return supportCluster;
    }

    public void setSupportCluster(boolean supportCluster) {
        this.supportCluster = supportCluster;
    }

    private double queryBandwidth(SubstrateLink substrateLink) {
        BandwidthResource bandwidthResource = substrateLink.getBandwidthResource();  // substrateLink.getSingle(BandwidthResource.class);
        return bandwidthResource == null ? 0 : bandwidthResource.getAvailableBandwidth();
    }

    private double queryCpu(SubstrateNode substrateNode) {
        CpuResource cpuResource = substrateNode.getCpuResource();  // substrateNode.getSingle(CpuResource.class);
        return cpuResource == null ? 0 : cpuResource.getAvailableCycles();
    }

    @Override
    public VirtualNetwork coarsen(VirtualNetwork toCoarsen, SubstrateNetwork substrateNetwork) {
        startTime = System.nanoTime();
        CoarsenedVirtualNetwork coarsened = new CoarsenedVirtualNetwork(toCoarsen.getLayer());

        // 克隆
        coarsened.setBaseNetwork(toCoarsen);
        toCoarsen.copyTo(coarsened);
        copyTime = System.nanoTime();

        // 获取底层资源视图
        CoarsenMetric coarsenMetric = new CoarsenMetric(coarsened, toCoarsen, substrateNetwork);
        metricTime = System.nanoTime();

        // 粗化
        doCoarsen(coarsenMetric, coarsened);

        return coarsened;
    }

    /**
     * 评价边，越大合并越优
     *
     * @param virtualNetwork 虚拟网络
     * @param virtualLink    虚拟链路
     * @param maxCpu         最大CPU
     * @param maxBandwidth   最大带宽
     * @return 边得分
     */
    private double scoreEdge(VirtualNetwork virtualNetwork, VirtualLink virtualLink, double maxCpu, double maxBandwidth) {
        double cpu1 = queryCpu(virtualNetwork.getSource(virtualLink)) / maxCpu;
        double cpu2 = queryCpu(virtualNetwork.getDest(virtualLink)) / maxCpu;
        double bandwidth = queryBandwidth(virtualLink) / maxBandwidth;
        return bandwidth - (cpu1 + cpu2);
    }

    /**
     * 获取排序好的边列表
     *
     * @param virtualNetwork 虚拟网络
     * @param coarsenMetric  粗化参数
     * @return 列表
     */
    private List<VirtualLink> getSortedEdges(final VirtualNetwork virtualNetwork, final CoarsenMetric coarsenMetric) {
        List<VirtualLink> virtualLinks = new ArrayList<VirtualLink>(virtualNetwork.getEdges());
        Collections.sort(virtualLinks, new Comparator<VirtualLink>() {
            @Override
            public int compare(VirtualLink o1, VirtualLink o2) {
                double inter = scoreEdge(virtualNetwork, o2, coarsenMetric.maxCpu, coarsenMetric.maxBandwidth)
                        - scoreEdge(virtualNetwork, o1, coarsenMetric.maxCpu, coarsenMetric.maxBandwidth);
                if (inter > 0) return 1;
                if (inter < 0) return -1;
                return 0;
            }
        });

        return virtualLinks;
    }

    private void doCoarsen(final CoarsenMetric coarsenMetric,
                           final CoarsenedVirtualNetwork coarsened) {

        List<VirtualLink> virtualLinks = getSortedEdges(coarsened, coarsenMetric);
        sortTime = System.nanoTime();

        // 为了效率，只在外面创建一次
        Map<VirtualNode, List<VirtualLink>> linksOfNode = new HashMap<VirtualNode, List<VirtualLink>>();

        // 遍历连接，合并节点与连接
        for (VirtualLink virtualLink : virtualLinks) {
            VirtualNode node1;
            VirtualNode node2;
            if (!coarsened.containsEdge(virtualLink)) {
                if (!supportCluster) {
                    // 已经在合并过程中去除了
                    // 阻止了已合并节点的再次合并
                    continue;
                }

                // 两个粗化节点就不能再合并了
                node1 = coarsenMetric.originalVirtualNetwork.getSource(virtualLink);
                node2 = coarsenMetric.originalVirtualNetwork.getDest(virtualLink);
                if (coarsenMetric.coarsenMap.containsKey(node1)) {
                    node1 = coarsenMetric.coarsenMap.get(node1);
                }
                if (coarsenMetric.coarsenMap.containsKey(node2)) {
                    node2 = coarsenMetric.coarsenMap.get(node2);
                }
            } else {
                node1 = coarsened.getSource(virtualLink);
                node2 = coarsened.getDest(virtualLink);
            }

            if (node1 == node2) {
                continue;
            }

            if (!coarsenMetric.canCombine(node1, node2)) {
                continue;
            }

            combineNodes(coarsenMetric, coarsened, linksOfNode, virtualLink, node1, node2);
        }

        combineTime = System.nanoTime();
    }

    private CoarsenVirtualNode combineNodes(CoarsenMetric coarsenMetric,
                                            CoarsenedVirtualNetwork coarsened,
                                            Map<VirtualNode, List<VirtualLink>> linksOfNode,
                                            VirtualLink virtualLink,
                                            VirtualNode node1, VirtualNode node2) {
        // 1. 合并节点
        List<VirtualNode> subNodes = new ArrayList<>();
        if (isCoarsenNodeOfCurrent(coarsened, node1)) {
            subNodes.addAll(((CoarsenVirtualNode) node1).getAllSub());
        } else {
            subNodes.add(node1);
        }

        if (isCoarsenNodeOfCurrent(coarsened, node2)) {
            subNodes.addAll(((CoarsenVirtualNode) node2).getAllSub());
        } else {
            subNodes.add(node2);
        }

        CoarsenVirtualNode coarsenVirtualNode = new CoarsenVirtualNode(NetworkEntity.allocateId(), subNodes.toArray(new VirtualNode[subNodes.size()]));
        coarsenVirtualNode.setCoarsenLevel(coarsened.getCoarsenLevel());
        for (VirtualNode subNode : subNodes) {
            coarsenMetric.coarsenMap.put(subNode, coarsenVirtualNode);
        }

        coarsened.addVertex(coarsenVirtualNode);
        coarsened.removeEdge(virtualLink);
        // log(String.format("合并节点%s,%s", node1.getId(), node2.getId()));

        // TODO 处理合并粗化节点和粗化连接的情况
        // 2. 合并连接
        linksOfNode.clear();
        handleLinksOfNode(node1, coarsened, linksOfNode, node2);
        handleLinksOfNode(node2, coarsened, linksOfNode, node1);

        for (VirtualNode virtualNode : linksOfNode.keySet()) {
            List<VirtualLink> links = linksOfNode.get(virtualNode);
            CoarsenVirtualLink coarsenVirtualLink = new CoarsenVirtualLink(NetworkEntity.allocateId(),
                    links.toArray(new VirtualLink[links.size()]));
            // log("合并连接: " + list2String(links));
            // log("带宽: " + coarsenVirtualLink.getSingle(CoarsenBandwidthDemand.class).getDemandedBandwidth());
            coarsenVirtualLink.setCoarsenLevel(coarsened.getCoarsenLevel());
            coarsened.addEdge(coarsenVirtualLink, virtualNode, coarsenVirtualNode);
        }

        coarsened.removeVertex(node1);
        coarsened.removeVertex(node2);

        coarsenMetric.updateAdjacentBandwidthMap(coarsenVirtualNode);

        return coarsenVirtualNode;
    }

    private <T> String list2String(List<T> list) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (T t : list) {
            if (first) {
                first = false;
            } else {
                sb.append(',');
            }
            sb.append(t.toString());
        }
        return sb.toString();
    }

    private void log(String message) {
        if (useLog) {
            System.out.println(message);
        }
    }

    public void useLog(boolean useLog) {
        this.useLog = useLog;
    }

    private boolean isCoarsenNodeOfCurrent(CoarsenedVirtualNetwork coarsened, VirtualNode node) {
        return node instanceof CoarsenVirtualNode && ((CoarsenVirtualNode) node).getCoarsenLevel() == coarsened.getCoarsenLevel();
    }

    private boolean isCoarsenLinkOfCurrent(CoarsenedVirtualNetwork coarsened, VirtualLink link) {
        return link instanceof CoarsenVirtualLink && ((CoarsenVirtualLink) link).getCoarsenLevel() == coarsened.getCoarsenLevel();
    }

    private void handleLinksOfNode(VirtualNode node1, CoarsenedVirtualNetwork coarsened, Map<VirtualNode, List<VirtualLink>> linksOfNode, VirtualNode node2) {
        // 合并节点
        for (VirtualLink linkOfNode : new ArrayList<VirtualLink>(coarsened.getInEdges(node1))) {
            VirtualNode anotherNode = coarsened.getSource(linkOfNode);
            if (anotherNode == node2) {
                // another link connects these two nodes, just remove it
                coarsened.removeEdge(linkOfNode);
                continue;
            }

            addTo(coarsened, linksOfNode, linkOfNode, anotherNode);
            coarsened.removeEdge(linkOfNode);
        }

        for (VirtualLink linkOfNode : new ArrayList<VirtualLink>(coarsened.getOutEdges(node1))) {
            VirtualNode anotherNode = coarsened.getDest(linkOfNode);
            if (anotherNode == node2) {
                coarsened.removeEdge(linkOfNode);
                continue;
            }

            addTo(coarsened, linksOfNode, linkOfNode, anotherNode);
            coarsened.removeEdge(linkOfNode);
        }
    }

    private void addTo(CoarsenedVirtualNetwork coarsened, Map<VirtualNode, List<VirtualLink>> linksOfNode, VirtualLink linkOfNode, VirtualNode anotherNode) {
        if (isCoarsenLinkOfCurrent(coarsened, linkOfNode)) {
            for (VirtualLink virtualLink : ((CoarsenVirtualLink) linkOfNode).getAllSub()) {
                addTo(linksOfNode, anotherNode, linkOfNode);
            }
        } else {
            addTo(linksOfNode, anotherNode, linkOfNode);
        }
    }

    private void addTo(Map<VirtualNode, List<VirtualLink>> linksOfNode, VirtualNode anotherNode, VirtualLink linkOfNode) {
        List<VirtualLink> links = linksOfNode.get(anotherNode);
        if (links == null) {
            links = new ArrayList<>();
            linksOfNode.put(anotherNode, links);
        }

        links.add(linkOfNode);
    }

    private static double queryBandwidth(VirtualLink virtualLink) {
        BandwidthDemand bandwidthDemand = virtualLink.getSingle(BandwidthDemand.class);
        return bandwidthDemand != null ? bandwidthDemand.getDemandedBandwidth() : 0;
    }

    private static double queryCpu(VirtualNode virtualNode) {
        CpuDemand cpuDemand = virtualNode.getSingle(CpuDemand.class);
        return cpuDemand != null ? cpuDemand.getDemandedCycles() : 0;
    }

    private MiscelFunctions.MinMax<Double> minMaxBandwidth(SubstrateNetwork substrateNetwork) {
        return MiscelFunctions.findMinMaxValueOfLink(substrateNetwork, new Transformer<SubstrateLink, Double>() {
            @Override
            public Double transform(SubstrateLink abstractResources) {
                BandwidthResource bandwidthResource = abstractResources.getSingle(BandwidthResource.class);
                return bandwidthResource != null ? bandwidthResource.getAvailableBandwidth() : 0;
            }
        }, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    private MiscelFunctions.MinMax<Double> minMaxCpu(SubstrateNetwork substrateNetwork) {
        return MiscelFunctions.findMinMaxValueOfNode(substrateNetwork, new Transformer<SubstrateNode, Double>() {
            @Override
            public Double transform(SubstrateNode substrateNode) {
                CpuResource cpuResource = substrateNode.getSingle(CpuResource.class);
                return cpuResource != null ? cpuResource.getAvailableCycles() : 0;
            }
        }, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    private void coarsenNode(CoarsenVirtualNode coarsenedNode, VirtualNode toCoarsen) {
        coarsenedNode.addSub(toCoarsen);
    }

    @Override
    public VirtualNetwork undoCoarsen(VirtualNetwork coarsened) {
        // TODO 处理多层，可以尝试添加修正环节
        return undoCoarsenOneLevel(coarsened);
    }

    private VirtualNetwork undoCoarsenOneLevel(final VirtualNetwork coarsened) {
        assert coarsened instanceof CoarsenedVirtualNetwork;
        VirtualNetwork originalNetwork = ((CoarsenedVirtualNetwork) coarsened).getBaseNetwork();

        // 处理节点资源映射
        for (VirtualNode virtualNode : coarsened.getVertices()) {
            if (!(virtualNode instanceof CoarsenVirtualNode)) {
                continue;
            }

            SubstrateNode substrateNode = querySubstrateNode(virtualNode);
            CoarsenVirtualNode coarsenNode = (CoarsenVirtualNode) virtualNode;
            undoCoarsenEntity(coarsenNode);

            if (substrateNode != null) {
                LocalBandwidthResource lbResource = substrateNode.getSingle(LocalBandwidthResource.class);
                if (lbResource == null) {
                    lbResource = new LocalBandwidthResource(substrateNode);
                    substrateNode.add(lbResource);
                }

                Set<VirtualNode> subNodes = new HashSet<>(coarsenNode.getAllSub());
                for (VirtualNode subNode : subNodes) {
                    // 处理内部连接，由于内部节点两端都在内部，所以只要遍历一侧就可以了
                    for (VirtualLink link : originalNetwork.getInEdges(subNode)) {
                        if (subNodes.contains(originalNetwork.getSource(link))) {
                            for (AbstractDemand demand : link.get()) {
                                if (lbResource.accepts(demand) && lbResource.fulfills(demand)) {
                                    demand.occupy(lbResource);
                                }
                            }
                        }
                    }
                }
            }
        }

        // 处理连接映射关系
        for (VirtualLink virtualLink : coarsened.getEdges()) {
            if (!(virtualLink instanceof CoarsenVirtualLink)) {
                continue;
            }

            CoarsenVirtualLink coarsenLink = (CoarsenVirtualLink) virtualLink;
            undoCoarsenEntity(coarsenLink);
        }

        return originalNetwork;
    }

    private SubstrateNode querySubstrateNode(VirtualNode virtualNode) {
        for (AbstractDemand abstractDemand : virtualNode.get()) {
            for (Mapping mapping : abstractDemand.getMappings()) {
                if (mapping.getResource() != null) {
                    return (SubstrateNode) mapping.getResource().getOwner();
                }
            }
        }

        return null;
    }

    private void undoCoarsenEntity(CoarsenEntity<? extends NetworkEntity<AbstractDemand>> coarsenEntity) {
        for (AbstractDemand demand : coarsenEntity.get()) {
            if (!(demand instanceof CoarsenedDemand)) {
                continue;
            }

            CoarsenedDemand<? extends AbstractDemand> coarsenedDemand = (CoarsenedDemand<? extends AbstractDemand>) demand;
            // mapping.unregister操作可能会影响到获取的mapping列表，因此复制一份
            for (Mapping mapping : new ArrayList<Mapping>(coarsenedDemand.getMappings())) {
                AbstractResource resource = mapping.getResource();
                demand.free(resource);
                for (AbstractDemand subDemand : coarsenedDemand.getDemands()) {
                    if (resource.accepts(subDemand) && resource.fulfills(subDemand)) {
                        subDemand.occupy(resource);
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        BasicCoarsening coarsening = new BasicCoarsening();
        coarsening.setSupportCluster(true);
        VirtualNetwork virtualNetwork = new RandomGtItmVirtualNetworkGenerator(4, 4, 1, 5, 10).create();
        SubstrateNetwork substrateNetwork = new RandomGtItmSubstrateNetworkGenerator(10, 10, 1, 80, 100).create();
        MappingAlgorithm mappingAlgorithm = new IsomorphismAlgorithm();
        VirtualNetwork coarsened = coarsening.coarsen(virtualNetwork, substrateNetwork);
        mappingAlgorithm.map(substrateNetwork, coarsened);
        System.out.println(SubstrateCostEvaluator.costAll(substrateNetwork));
        VirtualNetwork uncoarsened = coarsening.undoCoarsen(coarsened);
        System.out.println(SubstrateCostEvaluator.costAll(substrateNetwork));
        uncoarsened.freeAllDemands();
        System.out.println(coarsened.getVertexCount() / (double) virtualNetwork.getVertexCount());
        System.out.println(SubstrateCostEvaluator.costAll(substrateNetwork));
        coarsened.freeAllDemands();
        System.out.println(SubstrateCostEvaluator.costAll(substrateNetwork));
    }
}
