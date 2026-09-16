package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeListProxy;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeListProxyFactoryTypeRegistry;

/**
 * A base class for list proxy factories that use NBT to store data.
 *
 * @author rubensworks
 */
public abstract class ValueTypeListProxyNBTFactorySimple<T extends IValueType<V>, V extends IValue, P extends IValueTypeListProxy<T, V>>
    implements IValueTypeListProxyFactoryTypeRegistry.IProxyFactory<T, V, P> {

    @Override
    public NBTBase serialize(P value) throws IValueTypeListProxyFactoryTypeRegistry.SerializationException {
        NBTTagCompound tag = new NBTTagCompound();
        serializeNbt(value, tag);
        return tag;
    }

    @Override
    public P deserialize(NBTBase value) throws IValueTypeListProxyFactoryTypeRegistry.SerializationException {
        try {
            return deserializeNbt((NBTTagCompound) value);
        } catch (ClassCastException | EvaluationException e) {
            e.printStackTrace();
            throw new IValueTypeListProxyFactoryTypeRegistry.SerializationException(e.getMessage());
        }
    }

    protected abstract void serializeNbt(P value, NBTTagCompound tag)
        throws IValueTypeListProxyFactoryTypeRegistry.SerializationException;

    protected abstract P deserializeNbt(NBTTagCompound tag)
        throws IValueTypeListProxyFactoryTypeRegistry.SerializationException, EvaluationException;
}
