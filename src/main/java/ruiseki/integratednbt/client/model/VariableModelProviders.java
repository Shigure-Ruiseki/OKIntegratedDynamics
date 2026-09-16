package ruiseki.integratednbt.client.model;

import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.client.model.IVariableModelProviderRegistry;
import ruiseki.integrateddynamics.core.client.model.SingleVariableModelProvider;
import ruiseki.integratednbt.Reference;

/**
 * Collection of variable model providers.
 *
 * @author rubensworks
 */
public class VariableModelProviders {

    public static final IVariableModelProviderRegistry REGISTRY = IntegratedDynamics._instance.getRegistryManager()
        .getRegistry(IVariableModelProviderRegistry.class);

    public static final SingleVariableModelProvider NBT_EXTRACTED = REGISTRY.addProvider(
        new SingleVariableModelProvider(new ResourceLocation(Reference.MOD_ID, "customoverlay/nbt_extracted")));

    public static void load() {}

}
