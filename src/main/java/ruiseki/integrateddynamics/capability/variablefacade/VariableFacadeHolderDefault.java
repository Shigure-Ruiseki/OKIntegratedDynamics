package ruiseki.integrateddynamics.capability.variablefacade;

import net.minecraft.item.ItemStack;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHolder;

/**
 * {@link IVariableFacadeHolder} that stores facade info in the root of the item's NBT tag.
 * 
 * @author rubensworks
 */
public class VariableFacadeHolderDefault implements IVariableFacadeHolder {

    private final ItemStack itemStack;

    public VariableFacadeHolderDefault(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public IVariableFacade getVariableFacade() {
        return IntegratedDynamics._instance.getRegistryManager()
            .getRegistry(IVariableFacadeHandlerRegistry.class)
            .handle(itemStack);
    }
}
