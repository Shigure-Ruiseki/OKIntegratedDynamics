package ruiseki.integratedcrafting.core;

import ruiseki.integratedcrafting.IntegratedCrafting;
import ruiseki.integratedcrafting.api.crafting.ICraftingProcessOverrideRegistry;
import ruiseki.integratedcrafting.core.crafting.processoverride.CraftingProcessOverrideBrewingStand;
import ruiseki.integratedcrafting.core.crafting.processoverride.CraftingProcessOverrideCraftingTable;

/**
 * @author rubensworks
 */
public class CraftingProcessOverrides {

    public static ICraftingProcessOverrideRegistry REGISTRY = IntegratedCrafting._instance.getRegistryManager()
        .getRegistry(ICraftingProcessOverrideRegistry.class);

    public static final CraftingProcessOverrideCraftingTable CRAFTING_TABLE = REGISTRY
        .register(new CraftingProcessOverrideCraftingTable());
    public static final CraftingProcessOverrideBrewingStand BREWING_STAND = REGISTRY
        .register(new CraftingProcessOverrideBrewingStand());

    public static void load() {}

}
