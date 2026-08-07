package ruiseki.integrateddynamics.capability;

import java.util.Map;

import com.google.common.collect.Maps;

import ruiseki.integrateddynamics.api.block.IVariableContainer;
import ruiseki.integrateddynamics.api.item.IVariableFacade;

/**
 * Default implementation of {@link IVariableContainer}.
 * 
 * @author rubensworks
 */
public class VariableContainerDefault implements IVariableContainer {

    private final Map<Integer, IVariableFacade> variableCache = Maps.newHashMap();

    @Override
    public Map<Integer, IVariableFacade> getVariableCache() {
        return this.variableCache;
    }
}
