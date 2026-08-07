package ruiseki.integrateddynamics.core.path;

import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Sets;

import ruiseki.integrateddynamics.api.path.IPathElement;
import ruiseki.okcore.datastructure.DimPos;

/**
 * Algorithm to construct paths/clusters of {@link IPathElement}s.
 * 
 * @author rubensworks
 */
public final class PathFinder {

    protected static TreeSet<IPathElement> getConnectedElements(IPathElement head, Set<DimPos> visitedPositions) {
        TreeSet<IPathElement> elements = Sets.newTreeSet();

        // Make sure to add our head
        if (!visitedPositions.contains(head.getPosition())) {
            elements.add(head);
            visitedPositions.add(head.getPosition());
        }

        // Add neighbours that haven't been checked yet.
        for (IPathElement neighbour : head.getReachableElements()) {
            if (!visitedPositions.contains(neighbour.getPosition())) {
                elements.add(neighbour);
                visitedPositions.add(neighbour.getPosition());
            }
        }

        // Loop over the added neighbours to recursively check their neighbours.
        Set<IPathElement> neighbourElements = Sets.newHashSet();
        for (IPathElement addedElement : elements) {
            neighbourElements.addAll(getConnectedElements(addedElement, visitedPositions));
        }
        elements.addAll(neighbourElements);

        return elements;
    }

    public static Cluster getConnectedCluster(IPathElement head) {
        return new Cluster(getConnectedElements(head, Sets.<DimPos>newTreeSet()));
    }

}
