package ruiseki.integratedrest.client.model;

import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.client.model.IVariableModelProviderRegistry;
import ruiseki.integrateddynamics.core.client.model.SingleVariableModelProvider;
import ruiseki.integratedrest.Reference;

/**
 * Collection of variable model providers.
 * 
 * @author rubensworks
 */
public class HttpVariableModelProviders {

    public static final IVariableModelProviderRegistry REGISTRY = IntegratedDynamics._instance.getRegistryManager()
        .getRegistry(IVariableModelProviderRegistry.class);
    public static final SingleVariableModelProvider HTTP = REGISTRY
        .addProvider(new SingleVariableModelProvider(new ResourceLocation(Reference.MOD_ID, "customoverlay/http")));

    public static void load() {}

}
