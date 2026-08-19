package ruiseki.integrateddynamics.capability.variablecontainer;

import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import com.google.common.collect.Maps;

import ruiseki.integrateddynamics.api.block.IVariableContainer;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.core.network.event.VariableContentsUpdatedEvent;
import ruiseki.integrateddynamics.item.ItemVariable;
import ruiseki.integrateddynamics.item.ItemVariableConfig;

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

    @Override
    public void refreshVariables(INetwork network, IInventory inventory, boolean sendVariablesUpdateEvent) {
        // Invalidate variables
        IPartNetwork partNetwork = NetworkHelpers.getPartNetwork(network);
        if (partNetwork != null) {
            for (IVariableFacade variableFacade : getVariableCache().values()) {
                IVariable<?> variable = variableFacade.getVariable(partNetwork);
                if (variable != null) {
                    variable.invalidate();
                }
            }
        }

        // Reset variable facades in inventory
        getVariableCache().clear();
        IVariableFacade firstInvalidVariableFacade = null;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack itemStack = inventory.getStackInSlot(i);
            if (itemStack != null) {
                IVariableFacade variableFacade = ((ItemVariable) ItemVariableConfig._instance.getInstance())
                    .getVariableFacade(itemStack);
                if (variableFacade != null) {
                    if (variableFacade.isValid()) {
                        getVariableCache().put(variableFacade.getId(), variableFacade);
                    } else if (firstInvalidVariableFacade == null) {
                        firstInvalidVariableFacade = variableFacade;
                    }
                }
            }
        }

        // If no valid variables were present, fallback to the first invalid variable facade.
        // This is for example to make sure that empty variables are resolved to true.
        if (getVariableCache().isEmpty() && firstInvalidVariableFacade != null) {
            getVariableCache().put(firstInvalidVariableFacade.getId(), firstInvalidVariableFacade);
        }

        // Trigger event in network
        if (sendVariablesUpdateEvent) {
            if (network != null) {
                network.getEventBus()
                    .post(new VariableContentsUpdatedEvent(network));
            }
        }
    }
}
