package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.ImmutableList;

import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeListProxyFactoryTypeRegistry;

/**
 * Factory for {@link ValueTypeListProxyMaterialized}.
 *
 * @author rubensworks
 */
public class ValueTypeListProxyMaterializedFactory implements
    IValueTypeListProxyFactoryTypeRegistry.IProxyFactory<IValueType<IValue>, IValue, ValueTypeListProxyMaterialized<IValueType<IValue>, IValue>> {

    private static final String ELEMENT_DELIMITER = ";";
    private static final String ELEMENT_DELIMITER_SPLITREGEX = "(?<!\\\\);";
    private static final String ELEMENT_DELIMITER_ESCAPED = "\\\\;";

    @Override
    public ResourceLocation getName() {
        return new ResourceLocation(Reference.MOD_ID, "materialized");
    }

    @Override
    public NBTBase serialize(ValueTypeListProxyMaterialized<IValueType<IValue>, IValue> values)
        throws IValueTypeListProxyFactoryTypeRegistry.SerializationException {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList list = new NBTTagList();

        // Store headers
        IValueType<IValue> valueType = values.getValueType();
        boolean heterogeneous = false;
        try {
            // Hack to avoid issue where categories are sometimes used to serialize/deserialize,
            // which is not allowed (and will crash during deserialization #570).
            if (valueType.isCategory() && values.getLength() > 0) {
                heterogeneous = true;
            }
        } catch (EvaluationException e) {}
        tag.setString(
            "valueType",
            valueType.getUniqueName()
                .toString());
        tag.setTag("values", list);

        // Store values
        for (IValue value : values) {
            NBTBase valueSerialized = ValueHelpers.serializeRaw(value);
            if (heterogeneous) {
                NBTTagCompound valueTag = new NBTTagCompound();
                valueTag.setString(
                    "valueType",
                    value.getType()
                        .getUniqueName()
                        .toString());
                valueTag.setTag("value", valueSerialized);
                list.appendTag(valueTag);
            } else {
                list.appendTag(valueSerialized);
            }
        }

        return tag;
    }

    @Override
    public ValueTypeListProxyMaterialized<IValueType<IValue>, IValue> deserialize(NBTBase value)
        throws IValueTypeListProxyFactoryTypeRegistry.SerializationException {
        if (!(value instanceof NBTTagCompound)) {
            throw new IValueTypeListProxyFactoryTypeRegistry.SerializationException(
                String.format(
                    "Could not deserialize the materialized list value '%s' as it is not a CompoundTag.",
                    value));
        }
        NBTTagCompound tag = (NBTTagCompound) value;
        if (!tag.hasKey("valueType")) {
            throw new IValueTypeListProxyFactoryTypeRegistry.SerializationException(
                String.format(
                    "Could not deserialize the materialized list value '%s' as it is missing a valueType.",
                    value));
        }
        if (!tag.hasKey("values")) {
            throw new IValueTypeListProxyFactoryTypeRegistry.SerializationException(
                String
                    .format("Could not deserialize the materialized list value '%s' as it is missing values.", value));
        }

        String valueTypeName = tag.getString("valueType");
        IValueType<IValue> valueType = ValueTypes.REGISTRY.getValueType(new ResourceLocation(valueTypeName));
        if (valueType == null) {
            throw new IValueTypeListProxyFactoryTypeRegistry.SerializationException(
                String.format(
                    "Could not deserialize the serialized materialized list proxy value because the value type by name '%s' was not found.",
                    valueTypeName));
        }

        boolean heterogeneous = valueType.isCategory();
        IValueType<IValue> elementValueType = valueType;

        ImmutableList.Builder<IValue> builder = ImmutableList.builder();
        NBTTagList list = (NBTTagList) tag.getTag("values");
        for (Object valueTag : list.tagList) {
            NBTBase valueSerialized;
            if (heterogeneous) {
                String subValueTypeName = ((NBTTagCompound) valueTag).getString("valueType");
                elementValueType = ValueTypes.REGISTRY.getValueType(new ResourceLocation(subValueTypeName));
                if (elementValueType == null) {
                    throw new IValueTypeListProxyFactoryTypeRegistry.SerializationException(
                        String.format(
                            "Could not deserialize the serialized materialized list proxy value because the value type by name '%s' was not found.",
                            subValueTypeName));
                }
                valueSerialized = ((NBTTagCompound) valueTag).getTag("value");
            } else {
                valueSerialized = (NBTBase) valueTag;
            }
            IValue deserializedValue = ValueHelpers.deserializeRaw(elementValueType, valueSerialized);
            builder.add(deserializedValue);
        }

        return new ValueTypeListProxyMaterialized<>(valueType, builder.build());
    }
}
