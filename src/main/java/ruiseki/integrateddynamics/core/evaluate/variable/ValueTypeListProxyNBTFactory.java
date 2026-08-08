package ruiseki.integrateddynamics.core.evaluate.variable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeListProxy;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeListProxyFactoryTypeRegistry;
import ruiseki.okcore.persist.nbt.INBTProvider;

/**
 * Factory for list proxies that implement {@link ruiseki.okcore.persist.nbt.INBTProvider}.
 *
 * @author rubensworks
 */
public class ValueTypeListProxyNBTFactory<T extends IValueType<V>, V extends IValue, P extends IValueTypeListProxy<T, V> & INBTProvider>
    implements IValueTypeListProxyFactoryTypeRegistry.IProxyFactory<T, V, P> {

    private final String name;
    private final Class<P> proxyClass;

    public ValueTypeListProxyNBTFactory(String name, Class<P> proxyClass) {
        this.name = name;
        this.proxyClass = proxyClass;
    }

    @Override
    public String getName() {
        return this.name;
    }

    protected Class<P> getProxyClass() {
        return this.proxyClass;
    }

    @Override
    public String serialize(P values) throws IValueTypeListProxyFactoryTypeRegistry.SerializationException {
        NBTTagCompound tag = new NBTTagCompound();
        values.writeGeneratedFieldsToNBT(tag);
        return tag.toString();
    }

    @Override
    public P deserialize(String value) throws IValueTypeListProxyFactoryTypeRegistry.SerializationException {
        try {
            Constructor<P> constructor = getProxyClass().getConstructor();
            P proxy = constructor.newInstance();
            NBTTagCompound tag = (NBTTagCompound) JsonToNBT.func_150315_a(value);
            proxy.readGeneratedFieldsFromNBT(tag);
            return proxy;
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | NBTException
            | IllegalAccessException e) {
            e.printStackTrace();
            throw new IValueTypeListProxyFactoryTypeRegistry.SerializationException(e.getMessage());
        }
    }
}
