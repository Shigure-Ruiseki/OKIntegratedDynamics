package ruiseki.integrateddynamics.core.evaluate.variable;

import java.util.Optional;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.Reference;

/**
 * A list of NBT tags.
 */
public class ValueTypeListProxyNbtValueListTag
    extends ValueTypeListProxyNbtValueListGeneric<NBTTagList, ValueTypeNbt, ValueTypeNbt.ValueNbt> {

    public ValueTypeListProxyNbtValueListTag(String key, Optional<NBTBase> tag) {
        super(ValueTypeListProxyFactories.NBT_VALUE_LIST_TAG.getName(), ValueTypes.NBT, key, tag);
    }

    @Override
    protected int getLength(NBTTagList tag) {
        return tag.tagCount();
    }

    @Override
    protected ValueTypeNbt.ValueNbt get(NBTTagList tag, int index) {
        return ValueTypeNbt.ValueNbt.of(tag.getCompoundTagAt(index));
    }

    @Override
    protected NBTTagList getDefault() {
        return new NBTTagList();
    }

    public static class Factory extends
        ValueTypeListProxyNbtValueListGeneric.Factory<ValueTypeListProxyNbtValueListTag, NBTTagList, ValueTypeNbt, ValueTypeNbt.ValueNbt> {

        @Override
        public ResourceLocation getName() {
            return new ResourceLocation(Reference.MOD_ID, "nbt.list_value_tag");
        }

        @Override
        protected ValueTypeListProxyNbtValueListTag create(String key, Optional<NBTBase> tag) {
            return new ValueTypeListProxyNbtValueListTag(key, tag);
        }
    }
}
