package ruiseki.integrateddynamics.core.client.model;

import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.api.client.model.IVariableModelProviderRegistry;

public class VariableModelProviders {

    public static final IVariableModelProviderRegistry REGISTRY = IntegratedDynamics._instance.getRegistryManager()
        .getRegistry(IVariableModelProviderRegistry.class);

    public static final ValueTypeVariableModelProvider VALUETYPE = REGISTRY
        .addProvider(new ValueTypeVariableModelProvider());
    public static final AspectVariableModelProvider ASPECT = REGISTRY.addProvider(new AspectVariableModelProvider());
    public static final SingleVariableModelProvider PROXY = REGISTRY
        .addProvider(new SingleVariableModelProvider(new ResourceLocation(Reference.MOD_ID + ":customoverlay/proxy")));
    public static final SingleVariableModelProvider DELAY = REGISTRY
        .addProvider(new SingleVariableModelProvider(new ResourceLocation(Reference.MOD_ID + ":customoverlay/delay")));

    public static void load() {}
}
