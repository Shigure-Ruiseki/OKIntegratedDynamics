package ruiseki.integrateddynamics.core.client.model;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.api.client.model.IVariableModelProvider;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.part.aspect.Aspects;

public class AspectVariableModelProvider implements IVariableModelProvider<IAspect> {

    private final Map<IAspect, IIcon> icons = new HashMap<>();

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        icons.clear();
        for (IAspect aspect : Aspects.REGISTRY.getAspects()) {
            ResourceLocation modelPath = Aspects.REGISTRY.getAspectModel(aspect);
            if (modelPath != null) {
                String texturePath = ModelUtils.getLayer0FromModel(modelPath);
                if (texturePath != null && !texturePath.trim()
                    .isEmpty()) {
                    IIcon icon = iconRegister.registerIcon(texturePath);
                    if (icon != null) {
                        icons.put(aspect, icon);
                    }
                }
            }
        }
    }

    @Override
    public IIcon getIcon(IAspect aspect) {
        return icons.get(aspect);
    }
}
