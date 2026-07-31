package ruiseki.integrateddynamics.api.client.model;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IVariableModelProvider<T> {

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister);

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(T key);
}
