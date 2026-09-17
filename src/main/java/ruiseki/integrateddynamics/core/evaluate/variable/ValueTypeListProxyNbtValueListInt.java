package ruiseki.integrateddynamics.core.evaluate.variable;

import java.util.Optional;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.Reference;

/**
 * An NBT int array wrapper
 */
public class ValueTypeListProxyNbtValueListInt
    extends ValueTypeListProxyNbtValueListGeneric<NBTTagIntArray, ValueTypeInteger, ValueTypeInteger.ValueInteger> {

    public ValueTypeListProxyNbtValueListInt(String key, Optional<NBTBase> tag) {
        super(ValueTypeListProxyFactories.NBT_VALUE_LIST_INT.getName(), ValueTypes.INTEGER, key, tag);
    }

    @Override
    protected int getLength(NBTTagIntArray tag) {
        return tag.func_150302_c().length;
    }

    @Override
    protected ValueTypeInteger.ValueInteger get(NBTTagIntArray tag, int index) {
        return ValueTypeInteger.ValueInteger.of(tag.func_150302_c()[index]);
    }

    @Override
    protected NBTTagIntArray getDefault() {
        return new NBTTagIntArray(new int[0]);
    }

    public static class Factory extends
        ValueTypeListProxyNbtValueListGeneric.Factory<ValueTypeListProxyNbtValueListInt, NBTTagIntArray, ValueTypeInteger, ValueTypeInteger.ValueInteger> {

        @Override
        public ResourceLocation getName() {
            return new ResourceLocation(Reference.MOD_ID, "nbt.list_value_int");
        }

        @Override
        protected ValueTypeListProxyNbtValueListInt create(String key, Optional<NBTBase> tag) {
            return new ValueTypeListProxyNbtValueListInt(key, tag);
        }
    }
}
