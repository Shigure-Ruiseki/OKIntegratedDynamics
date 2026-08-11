package ruiseki.integrateddynamics.api.block;

import java.util.Map;

import net.minecraft.inventory.IInventory;

import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.network.INetwork;

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

    /**
     * Invalidate variables in this cache, clear the cache and re-populate from the supplied inventory
     * 
     * @param network                  {@link INetwork} that the variables are in
     * @param inventory                IInventory to re-populate the cache from
     * @param sendVariablesUpdateEvent if true post a VariableContentsUpdatedEvent to the network when done
     */
    public void refreshVariables(INetwork network, IInventory inventory, boolean sendVariablesUpdateEvent);
}
