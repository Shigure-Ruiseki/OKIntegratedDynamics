package ruiseki.integratedtunnels.core.network;

import net.minecraft.item.ItemStack;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.core.network.PositionedAddonsNetworkIngredients;
import ruiseki.integratedtunnels.api.network.IItemNetwork;

/**
 * A network that can hold items.
 * 
 * @author rubensworks
 */
public class ItemNetwork extends PositionedAddonsNetworkIngredients<ItemStack, Integer> implements IItemNetwork {

    public ItemNetwork(IngredientComponent<ItemStack, Integer> component) {
        super(component);
    }

    @Override
    public long getRateLimit() {
        return 64;
    }
}
