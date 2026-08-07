package ruiseki.integrateddynamics.api.block;

import java.util.Map;

import ruiseki.integrateddynamics.api.item.IVariableFacade;

/**
 * Capability that can hold {@link IVariableFacade}s.
 *
 * @author rubensworks
 */
public interface IVariableContainer {

    /**
     * @return The stored variable facades for this part.
     */
    public Map<Integer, IVariableFacade> getVariableCache();

}
