package ruiseki.integrateddynamics.core.client.model;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.api.client.model.IVariableModelProvider;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;

public class ValueTypeVariableModelProvider implements IVariableModelProvider<IValueType> {

    private final Map<IValueType, IIcon> icons = new HashMap<>();

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        icons.clear();
        for (IValueType valueType : ValueTypes.REGISTRY.getValueTypes()) {
            ResourceLocation modelPath = ValueTypes.REGISTRY.getValueTypeModel(valueType);
            if (modelPath != null) {
                String texturePath = ModelUtils.getLayer0FromModel(modelPath);
                if (texturePath != null && !texturePath.trim()
                    .isEmpty()) {
                    IIcon icon = iconRegister.registerIcon(texturePath);
                    if (icon != null) {
                        icons.put(valueType, icon);
                    }
                }
            }
        }
    }

    @Override
    public IIcon getIcon(IValueType valueType) {
        return icons.get(valueType);
    }
}
