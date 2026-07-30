package ruiseki.integrateddynamics.core.evaluate.variable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeCategory;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeRegistry;
import ruiseki.integrateddynamics.api.item.IValueTypeVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHandlerRegistry;
import ruiseki.okcore.helper.MinecraftHelpers;

/**
 * Registry for {@link IValueType}.
 *
 * @author rubensworks
 */
public final class ValueTypeRegistry implements IValueTypeRegistry {

    private static ValueTypeRegistry INSTANCE = new ValueTypeRegistry();
    private static final IValueTypeVariableFacade INVALID_FACADE = new ValueTypeVariableFacade(
        false,
        null,
        (IValue) null);

    private final Map<String, IValueType> valueTypes = Maps.newHashMap();
    @SideOnly(Side.CLIENT)
    private Map<IValueType, ResourceLocation> valueTypeModels;

    private ValueTypeRegistry() {
        if (MinecraftHelpers.isClientSide()) {
            valueTypeModels = Maps.newHashMap();
        }
        if (MinecraftHelpers.isModdedEnvironment()) {
            IntegratedDynamics._instance.getRegistryManager()
                .getRegistry(IVariableFacadeHandlerRegistry.class)
                .registerHandler(this);
        }
    }

    /**
     * @return The unique instance.
     */
    public static ValueTypeRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public <V extends IValue, T extends IValueType<V>> T register(T valueType) {
        valueTypes.put(valueType.getUnlocalizedName(), valueType);
        return valueType;
    }

    @Override
    public <V extends IValue, T extends IValueTypeCategory<V>> T registerCategory(T category) {
        return register(category);
    }

    @Override
    public IValueType getValueType(String name) {
        return valueTypes.get(name);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public <V extends IValue, T extends IValueType<V>> void registerValueTypeModel(T valueType,
        ResourceLocation modelLocation) {
        valueTypeModels.put(valueType, modelLocation);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public <V extends IValue, T extends IValueType<V>> ResourceLocation getValueTypeModel(T valueType) {
        return valueTypeModels.get(valueType);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public Collection<ResourceLocation> getValueTypeModels() {
        return Collections.unmodifiableCollection(valueTypeModels.values());
    }

    @Override
    public Collection<IValueType> getValueTypes() {
        return Collections.unmodifiableCollection(valueTypes.values());
    }

    @Override
    public String getTypeId() {
        return "valuetype";
    }

    @Override
    public IValueTypeVariableFacade getVariableFacade(int id, NBTTagCompound tag) {
        if (!tag.hasKey("typeName", MinecraftHelpers.NBTTag_Types.NBTTagString.ordinal())
            || !tag.hasKey("value", MinecraftHelpers.NBTTag_Types.NBTTagString.ordinal())) {
            return INVALID_FACADE;
        }
        IValueType type = getValueType(tag.getString("typeName"));
        if (type == null) {
            return INVALID_FACADE;
        }
        IValue value = type.deserialize(tag.getString("value"));
        return new ValueTypeVariableFacade(id, type, value);
    }

    @Override
    public void setVariableFacade(NBTTagCompound tag, IValueTypeVariableFacade variableFacade) {
        tag.setString(
            "typeName",
            variableFacade.getValueType()
                .getUnlocalizedName());
        tag.setString(
            "value",
            variableFacade.getValue()
                .getType()
                .serialize(variableFacade.getValue()));
    }
}
