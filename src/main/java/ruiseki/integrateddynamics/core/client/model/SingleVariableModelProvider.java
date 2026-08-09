package ruiseki.integrateddynamics.core.client.model;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.api.client.model.IVariableModelProvider;

public class SingleVariableModelProvider implements IVariableModelProvider<Void> {

    private IIcon icon;

    private final ResourceLocation model;

    public SingleVariableModelProvider(ResourceLocation model) {
        this.model = model;
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        this.icon = iconRegister.registerIcon(model.toString());
    }

    @Override
    public IIcon getIcon(Void key) {
        return icon;
    }
}
