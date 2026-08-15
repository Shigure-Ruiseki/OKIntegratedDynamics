package ruiseki.integratedterminals.core.terminalstorage;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.capability.network.PositionedAddonsNetworkIngredientsHandlerConfig;
import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.api.terminalstorage.ITerminalStorageTabRegistry;
import ruiseki.okcore.registries.RegistryEvent;

/**
 * @author rubensworks
 */
public class TerminalStorageTabs {

    public static ITerminalStorageTabRegistry REGISTRY = IntegratedTerminals._instance.getRegistryManager()
        .getRegistry(ITerminalStorageTabRegistry.class);

    public static void load() {
        MinecraftForge.EVENT_BUS.register(new TerminalStorageTabs());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void afterIngredientComponentsRegistration(RegistryEvent.Register event) {
        for (IngredientComponent<?, ?> ingredientComponent : IngredientComponent.REGISTRY.getValuesCollection()) {
            if (ingredientComponent.getCapability(PositionedAddonsNetworkIngredientsHandlerConfig.CAPABILITY)
                .isPresent()) {
                TerminalStorageTabs.REGISTRY.register(new TerminalStorageTabIngredientComponent<>(ingredientComponent));
            }
        }

        IngredientComponent<ItemStack, Integer> ingredientComponentItemStack = (IngredientComponent<ItemStack, Integer>) IngredientComponent.REGISTRY
            .getValue(new ResourceLocation("minecraft:itemstack"));

        if (ingredientComponentItemStack != null
            && ingredientComponentItemStack.getCapability(PositionedAddonsNetworkIngredientsHandlerConfig.CAPABILITY)
                .isPresent()) {
            TerminalStorageTabs.REGISTRY
                .register(new TerminalStorageTabIngredientComponentItemStackCrafting(ingredientComponentItemStack));
        }
    }

}
