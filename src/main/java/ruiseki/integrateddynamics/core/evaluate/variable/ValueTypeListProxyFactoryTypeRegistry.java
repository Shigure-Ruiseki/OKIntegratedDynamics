package ruiseki.integrateddynamics.core.evaluate.variable;

import java.util.Map;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.Maps;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeListProxy;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeListProxyFactoryTypeRegistry;

/**
 * Registry for list value type proxies.
 *
 * @author rubensworks
 */
public class ValueTypeListProxyFactoryTypeRegistry implements IValueTypeListProxyFactoryTypeRegistry {

    private static final String TYPE_DELIMITER = ";";
    private static final String TYPE_DELIMITER_SPLITREGEX = "(?<!\\\\);";
    private static final String TYPE_DELIMITER_ESCAPED = "\\\\;";

    private static ValueTypeListProxyFactoryTypeRegistry INSTANCE = new ValueTypeListProxyFactoryTypeRegistry();

    private final Map<String, IProxyFactory> factories = Maps.newHashMap();

    private ValueTypeListProxyFactoryTypeRegistry() {

    }

    /**
     * @return The unique instance.
     */
    public static ValueTypeListProxyFactoryTypeRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public <T extends IValueType<V>, V extends IValue, P extends IValueTypeListProxy<T, V>, F extends IProxyFactory<T, V, P>> F register(
        F proxyFactory) {
        if (factories.containsKey(proxyFactory.getName())) {
            throw new RuntimeException(
                String.format("A list proxy factory by name '%s' already exists.", proxyFactory.getName()));
        }
        factories.put(
            proxyFactory.getName()
                .toString(),
            proxyFactory);
        return proxyFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IValueType<V>, V extends IValue, P extends IValueTypeListProxy<T, V>> IProxyFactory<T, V, P> getFactory(
        ResourceLocation name) {
        return factories.get(name.toString());
    }

    @Override
    public <T extends IValueType<V>, V extends IValue, P extends IValueTypeListProxy<T, V>> NBTBase serialize(P proxy)
        throws SerializationException {
        IProxyFactory<T, V, P> factory = getFactory(proxy.getName());
        if (factory == null) {
            throw new SerializationException(
                String.format("No serialization factory exists for the list proxy type name '%s'.", proxy.getName()));
        }
        NBTBase serialized = factory.serialize(proxy);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString(
            "proxyName",
            proxy.getName()
                .toString());
        tag.setTag("serialized", serialized);
        return tag;
    }

    @Override
    public <T extends IValueType<V>, V extends IValue, P extends IValueTypeListProxy<T, V>> P deserialize(NBTBase value)
        throws SerializationException {
        if (!(value instanceof NBTTagCompound)) {
            throw new SerializationException(
                String.format(
                    "Could not deserialize the serialized list proxy value '%s' as it is not a CompoundTag.",
                    value));
        }
        NBTTagCompound tag = (NBTTagCompound) value;
        if (!tag.hasKey("proxyName")) {
            throw new SerializationException(
                String.format(
                    "Could not deserialize the serialized list proxy value '%s' as it is missing a proxyName.",
                    value));
        }
        if (!tag.hasKey("serialized")) {
            throw new SerializationException(
                String.format(
                    "Could not deserialize the serialized list proxy value '%s' as it is missing a serialized value.",
                    value));
        }
        String name = tag.getString("proxyName");
        NBTBase actualValue = tag.getTag("serialized");
        IProxyFactory<T, V, P> factory = getFactory(new ResourceLocation(name));
        if (factory == null) {
            throw new SerializationException(
                String.format("No deserialization factory exists for the list proxy type name '%s'.", name));
        }
        return factory.deserialize(actualValue);
    }
}
