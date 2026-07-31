package ruiseki.integrateddynamics.core.client.model;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.client.model.IVariableModelProviderRegistry;

public class VariableModelProviders {

    public static final IVariableModelProviderRegistry REGISTRY = IntegratedDynamics._instance.getRegistryManager()
        .getRegistry(IVariableModelProviderRegistry.class);

    public static final ValueTypeVariableModelProvider VALUETYPE = REGISTRY
        .addProvider(new ValueTypeVariableModelProvider());
    public static final AspectVariableModelProvider ASPECT = REGISTRY.addProvider(new AspectVariableModelProvider());
    public static final ProxyVariableModelProvider PROXY = REGISTRY.addProvider(new ProxyVariableModelProvider());

    public static void load() {}
}
