package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
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
    public String serialize(P value) throws IValueTypeListProxyFactoryTypeRegistry.SerializationException {
        NBTTagCompound tag = new NBTTagCompound();
        serializeNbt(value, tag);
        return tag.toString();
    }

    @Override
    public P deserialize(String value) throws IValueTypeListProxyFactoryTypeRegistry.SerializationException {
        try {
            NBTTagCompound tag = (NBTTagCompound) JsonToNBT.func_150315_a(value);
            return deserializeNbt(tag);
        } catch (NBTException | EvaluationException e) {
            e.printStackTrace();
            throw new IValueTypeListProxyFactoryTypeRegistry.SerializationException(e.getMessage());
        }
    }

    protected abstract void serializeNbt(P value, NBTTagCompound tag)
        throws IValueTypeListProxyFactoryTypeRegistry.SerializationException;

    protected abstract P deserializeNbt(NBTTagCompound tag)
        throws IValueTypeListProxyFactoryTypeRegistry.SerializationException, EvaluationException;
}
