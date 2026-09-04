package ruiseki.integrateddynamics.core.part.aspect.property;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Maps;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectPropertyTypeInstance;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.okcore.helper.MinecraftHelpers;

/**
 * A property that can be used inside aspects.
 *
 * @author rubensworks
 */
public class AspectProperties implements IAspectProperties {

    private final Map<IAspectPropertyTypeInstance, IValue> values = Maps.newLinkedHashMap();

    /**
     * Make a new instance.
     *
     * @param propertyTypes The types these properties will have. These will be used to initialize the default values.
     */
    public AspectProperties(Collection<IAspectPropertyTypeInstance> propertyTypes) {
        for (IAspectPropertyTypeInstance propertyType : propertyTypes) {
            values.put(
                propertyType,
                propertyType.getType()
                    .getDefault());
        }
    }

    /**
     * Only called for NBT serialization
     */
    public AspectProperties() {

    }

    @Override
    @Deprecated
    public Collection<IAspectPropertyTypeInstance> getTypes() {
        return Collections.unmodifiableCollection(values.keySet());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IValueType<V>, V extends IValue> V getValue(IAspectPropertyTypeInstance<T, V> type) {
        IValue value = values.get(type);
        if (value == null) {
            value = type.getType()
                .getDefault();
        }
        return (V) value;
    }

    @Override
    public <T extends IValueType<V>, V extends IValue> void setValue(IAspectPropertyTypeInstance<T, V> type, V value) {
        values.put(type, value);
    }

    @Override
    public <T extends IValueType<V>, V extends IValue> void removeValue(IAspectPropertyTypeInstance<T, V> type) {
        values.remove(type);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList map = new NBTTagList();
        for (Map.Entry<IAspectPropertyTypeInstance, IValue> entry : values.entrySet()) {
            NBTTagCompound nbtEntry = new NBTTagCompound();
            nbtEntry.setString(
                "key",
                entry.getKey()
                    .getType()
                    .getUnlocalizedName());
            nbtEntry.setString(
                "label",
                entry.getKey()
                    .getUnlocalizedName());
            nbtEntry.setString("value", ValueHelpers.serializeRaw(entry.getValue()));
            map.appendTag(nbtEntry);
        }
        tag.setTag("map", map);
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        values.clear();
        NBTTagList map = tag.getTagList("map", MinecraftHelpers.NBTTag_Types.NBTTagCompound.ordinal());
        for (int i = 0; i < map.tagCount(); i++) {
            NBTTagCompound nbtEntry = map.getCompoundTagAt(i);
            String valueTypeName = nbtEntry.getString("key");
            IValueType type = ValueTypes.REGISTRY.getValueType(valueTypeName);
            if (type == null) {
                IntegratedDynamics.clog(
                    Level.ERROR,
                    String.format("Could not find value type with name %s, skipping loading.", valueTypeName));
            } else {
                IValue value = ValueHelpers.deserializeRaw(type, nbtEntry.getString("value"));
                String label = nbtEntry.getString("label");
                if (value == null) {
                    IntegratedDynamics.clog(
                        Level.ERROR,
                        String.format("The value type %s could not load its value, using default.", valueTypeName));
                    value = type.getDefault();
                }
                values.put(new AspectPropertyTypeInstance(type, label), value);
            }
        }

    }

    @SuppressWarnings({ "CloneDoesntCallSuperClone", "deprecation" })
    @Override
    public IAspectProperties clone() {
        IAspectProperties clone = new AspectProperties(getTypes());
        for (IAspectPropertyTypeInstance type : getTypes()) {
            clone.setValue(type, getValue(type));
        }
        return clone;

    }
}
