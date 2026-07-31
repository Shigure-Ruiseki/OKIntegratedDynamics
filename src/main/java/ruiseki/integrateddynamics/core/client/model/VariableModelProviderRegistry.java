package ruiseki.integrateddynamics.core.client.model;

import java.util.Collections;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.client.model.IVariableModelProvider;
import ruiseki.integrateddynamics.api.client.model.IVariableModelProviderRegistry;

public class VariableModelProviderRegistry implements IVariableModelProviderRegistry {

    private static final VariableModelProviderRegistry INSTANCE = new VariableModelProviderRegistry();

    private final List<IVariableModelProvider<?>> providers = Lists.newArrayList();

    public static VariableModelProviderRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public <P extends IVariableModelProvider<?>> P addProvider(P provider) {
        providers.add(provider);
        return provider;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public List<IVariableModelProvider<?>> getProviders() {
        return Collections.unmodifiableList(providers);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        for (IVariableModelProvider<?> provider : providers) {
            provider.registerIcons(iconRegister);
        }
    }
}
