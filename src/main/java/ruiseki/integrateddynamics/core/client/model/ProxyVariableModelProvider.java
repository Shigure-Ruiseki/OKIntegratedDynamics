package ruiseki.integrateddynamics.core.client.model;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import ruiseki.integrateddynamics.api.client.model.IVariableModelProvider;

public class ProxyVariableModelProvider implements IVariableModelProvider<Void> {

    private IIcon icon;

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        this.icon = iconRegister.registerIcon("integrateddynamics:customoverlay/proxy");
    }

    @Override
    public IIcon getIcon(Void key) {
        return icon;
    }
}
