package ruiseki.integrateddynamics.api.client.model;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.init.IRegistry;

public interface IVariableModelProviderRegistry extends IRegistry {

    /**
     * Register a new icon provider.
     *
     * @param provider The provider to register.
     * @param <P>      The type of icon provider.
     * @return The registered provider.
     */
    public <P extends IVariableModelProvider<?>> P addProvider(P provider);

    /**
     * @return All registered providers.
     */
    @SideOnly(Side.CLIENT)
    public List<IVariableModelProvider<?>> getProviders();

    public void registerIcons(IIconRegister register);
}
