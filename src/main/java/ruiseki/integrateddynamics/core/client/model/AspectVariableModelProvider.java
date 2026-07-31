package ruiseki.integrateddynamics.core.client.model;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

import ruiseki.integrateddynamics.api.client.model.IVariableModelProvider;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.part.aspect.Aspects;

public class AspectVariableModelProvider implements IVariableModelProvider<IAspect> {

    private final Map<IAspect, IIcon> icons = new HashMap<>();

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        icons.clear();
        for (IAspect aspect : Aspects.REGISTRY.getAspects()) {
            String iconPath = Aspects.REGISTRY.getAspectIconPath(aspect);
            if (iconPath != null) {
                IIcon icon = iconRegister.registerIcon(iconPath);
                icons.put(aspect, icon);
            }
        }
    }

    @Override
    public IIcon getIcon(IAspect aspect) {
        return icons.get(aspect);
    }
}
