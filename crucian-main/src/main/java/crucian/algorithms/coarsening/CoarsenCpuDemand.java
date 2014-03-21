package crucian.algorithms.coarsening;

import vnreal.constraints.AbstractConstraint;
import vnreal.demands.CpuDemand;
import vnreal.network.Node;

import java.util.ArrayList;
import java.util.List;

public class CoarsenCpuDemand extends CpuDemand implements CoarsenedDemand<CpuDemand> {
    private List<CpuDemand> cpuDemands = new ArrayList<CpuDemand>();

    public CoarsenCpuDemand(Node<? extends AbstractConstraint> owner) {
        super(owner);
    }

    @Override
    public void addDemand(CpuDemand subDemand) {
        cpuDemands.add(subDemand);
        setDemandedCycles(getDemandedCycles() + subDemand.getDemandedCycles());
    }

    @Override
    public List<CpuDemand> getDemands() {
        return cpuDemands;
    }
}
