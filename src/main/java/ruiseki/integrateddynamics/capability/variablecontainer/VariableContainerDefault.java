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
                    if (variable.canInvalidate()) {
                        variable.invalidate();
                    }
                }
            }
        }

        // Reset variable facades in inventory
        getVariableCache().clear();
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack itemStack = inventory.getStackInSlot(i);
            if (itemStack != null) {
                IVariableFacade variableFacade = ItemVariable.getInstance()
                    .getVariableFacade(itemStack);
                if (variableFacade != null && variableFacade.isValid()) {
                    getVariableCache().put(variableFacade.getId(), variableFacade);
                }
            }
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
