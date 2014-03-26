package crucian.algorithms.coarsening;

import org.apache.commons.lang3.StringUtils;
import vnreal.demands.CpuDemand;
import vnreal.network.virtual.VirtualNode;

import java.util.ArrayList;
import java.util.List;

public class CoarsenVirtualNode extends VirtualNode implements CoarsenEntity<VirtualNode> {
    private List<VirtualNode> virtualNodeList = new ArrayList<VirtualNode>();
    private CoarsenCpuDemand coarsenCpuDemand = new CoarsenCpuDemand(this);
    private int coarsenLevel;

    public CoarsenVirtualNode(int id, int layer) {
        super(id, layer);
        add(coarsenCpuDemand);
    }

    public CoarsenVirtualNode(int id, VirtualNode[] subNodes) {
        this(id, subNodes[0].getLayer());
        for (VirtualNode virtualNode : subNodes) {
            addSub(virtualNode);
        }
        checkLayer();
    }

    @Override
    public void addSub(VirtualNode subNode) {
        virtualNodeList.add(subNode);
        for (CpuDemand cpuDemand : subNode.get(CpuDemand.class)) {
            coarsenCpuDemand.addDemand(cpuDemand);
        }
    }

    @Override
    public List<VirtualNode> getAllSub() {
        return virtualNodeList;
    }

    private void checkLayer() {
        for (VirtualNode virtualNode : virtualNodeList) {
            if (virtualNode.getLayer() != getLayer()) {
                throw new RuntimeException(String.format("子节点%d层级%d与父节点%d不同(%d)",
                        virtualNode.getId(), virtualNode.getLayer(), getId(), getLayer()));
            }
        }
    }

    public int getCoarsenLevel() {
        return coarsenLevel;
    }

    public void setCoarsenLevel(int coarsenLevel) {
        this.coarsenLevel = coarsenLevel;
    }

    @Override
    public String toString() {
        List<String> subIds = new ArrayList<String>();
        for (VirtualNode virtualNode : getAllSub()) {
            subIds.add(Integer.toString(virtualNode.getId()));
        }
        return String.format("VirtualNode(%d)[%s]@(%d)", getId(), StringUtils.join(subIds, ","), getLayer());
    }
}
