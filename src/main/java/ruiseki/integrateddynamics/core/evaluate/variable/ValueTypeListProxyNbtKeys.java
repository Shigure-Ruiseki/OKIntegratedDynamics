package ruiseki.integrateddynamics.core.evaluate.variable;

import java.util.Optional;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.Iterables;

import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeListProxyFactoryTypeRegistry;

/**
 * A list of NBT keys.
 */
public class ValueTypeListProxyNbtKeys extends ValueTypeListProxyBase<ValueTypeString, ValueTypeString.ValueString> {

    private final Optional<NBTBase> tag;

    public ValueTypeListProxyNbtKeys(Optional<NBTBase> tag) {
        super(ValueTypeListProxyFactories.NBT_KEYS.getName(), ValueTypes.STRING);
        this.tag = tag;
    }

    @Override
    public int getLength() throws EvaluationException {
        return tag.map(
            t -> t instanceof NBTTagCompound ? ((NBTTagCompound) t).func_150296_c()
                .size() : 0)
            .orElse(0);
    }

    @Override
    public ValueTypeString.ValueString get(int index) throws EvaluationException {
        if (index < getLength()) {
            return ValueTypeString.ValueString.of(Iterables.get(((NBTTagCompound) tag.get()).func_150296_c(), index));
        }
        return null;
    }

    public static class Factory extends
        ValueTypeListProxyNBTFactorySimple<ValueTypeString, ValueTypeString.ValueString, ValueTypeListProxyNbtKeys> {

        @Override
        public ResourceLocation getName() {
            return new ResourceLocation(Reference.MOD_ID, "nbt.keys");
        }

        @Override
        protected void serializeNbt(ValueTypeListProxyNbtKeys value, NBTTagCompound tag)
            throws IValueTypeListProxyFactoryTypeRegistry.SerializationException {
            value.tag.ifPresent(inbt -> tag.setTag("tag", inbt));
        }

        @Override
        protected ValueTypeListProxyNbtKeys deserializeNbt(NBTTagCompound tag)
            throws IValueTypeListProxyFactoryTypeRegistry.SerializationException {
            return new ValueTypeListProxyNbtKeys(tag.hasKey("tag") ? Optional.of(tag.getTag("tag")) : Optional.empty());
        }
    }
}
