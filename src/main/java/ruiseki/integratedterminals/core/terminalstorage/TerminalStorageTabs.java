package ruiseki.integratedterminals.core.terminalstorage;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.capability.network.PositionedAddonsNetworkIngredientsHandlerConfig;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabRegistry;

/**
 * @author rubensworks
 */
public class TerminalStorageTabs {

    public static ITerminalStorageTabRegistry REGISTRY = IntegratedTerminals._instance.getRegistryManager()
        .getRegistry(ITerminalStorageTabRegistry.class);

    @SuppressWarnings("unchecked")
    public static void load() {
        for (IngredientComponent<?, ?> ingredientComponent : IngredientComponent.REGISTRY.values()) {
            if (ingredientComponent.getCapability(PositionedAddonsNetworkIngredientsHandlerConfig.CAPABILITY)
                .isPresent()) {
                TerminalStorageTabs.REGISTRY.register(new TerminalStorageTabIngredientComponent<>(ingredientComponent));
            }
        }

        IngredientComponent<ItemStack, Integer> ingredientComponentItemStack = (IngredientComponent<ItemStack, Integer>) IngredientComponent.REGISTRY
            .get(new ResourceLocation("minecraft:itemstack"));

        if (ingredientComponentItemStack != null
            && ingredientComponentItemStack.getCapability(PositionedAddonsNetworkIngredientsHandlerConfig.CAPABILITY)
                .isPresent()) {
            TerminalStorageTabs.REGISTRY
                .register(new TerminalStorageTabIngredientComponentItemStackCrafting(ingredientComponentItemStack));
        }
    }

}
