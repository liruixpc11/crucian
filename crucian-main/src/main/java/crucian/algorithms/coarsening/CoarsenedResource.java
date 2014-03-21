package crucian.algorithms.coarsening;

import vnreal.mapping.Mapping;
import vnreal.resources.AbstractResource;

import java.util.List;

public interface CoarsenedResource<R extends AbstractResource> {
    public void addResource(R resource);

    public List<R> getResources();

    public List<Mapping> getMappings();
}
