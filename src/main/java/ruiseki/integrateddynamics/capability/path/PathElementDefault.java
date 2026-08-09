package ruiseki.integrateddynamics.capability.path;

import java.util.Collections;
import java.util.Set;

import ruiseki.integrateddynamics.api.path.IPathElement;

/**
 * Default implementation of {@link IPathElement}.
 *
 * @author rubensworks
 */
public abstract class PathElementDefault implements IPathElement {

    @Override
    public Set<IPathElement> getReachableElements() {
        return Collections.emptySet();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof IPathElement && compareTo(o) == 0;
    }

    @Override
    public int compareTo(Object o) {
        return getPosition().compareTo(((IPathElement) o).getPosition());
    }

    @Override
    public int hashCode() {
        return getPosition().hashCode();
    }
}
