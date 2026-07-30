package ruiseki.integrateddynamics.api.block;

import java.util.Map;

import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.okcore.datastructure.DimPos;

/**
 * An interface for containers that can hold {@link IVariableFacade}s.
 *
 * @author rubensworks
 */
public interface IVariableContainer {

    /**
     * @return The position this container is at.
     */
    public DimPos getPosition();

    /**
     * @return The stored variable facades for this tile.
     */
    public Map<Integer, IVariableFacade> getVariableCache();

}
