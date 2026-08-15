package ruiseki.integratedterminals.core.terminalstorage.crafting;

import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalStorageTabIngredientCraftingHandlerRegistry;

/**
 * @author rubensworks
 */
public class TerminalStorageTabIngredientCraftingHandlers {

    public static ITerminalStorageTabIngredientCraftingHandlerRegistry REGISTRY = IntegratedTerminals._instance
        .getRegistryManager()
        .getRegistry(ITerminalStorageTabIngredientCraftingHandlerRegistry.class);

    public static void load() {}

}
