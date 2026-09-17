package ruiseki.integrateddynamics.core.evaluate.variable;

import java.util.Optional;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeListProxyFactoryTypeRegistry;

/**
 * An abstraction for a list of NBT values of a certain type.
 */
public abstract class ValueTypeListProxyNbtValueListGeneric<N extends NBTBase, T extends IValueType<V>, V extends IValue>
    extends ValueTypeListProxyBase<T, V> {

    private final String key;
    private final Optional<NBTTagCompound> tag;

    public ValueTypeListProxyNbtValueListGeneric(ResourceLocation name, T valueType, String key,
        Optional<NBTBase> tag) {
        super(name, valueType);
        this.key = key;
        this.tag = tag.filter(t -> t instanceof NBTTagCompound)
            .map(t -> (NBTTagCompound) t);
    }

    public String getKey() {
        return key;
    }

    public Optional<NBTTagCompound> getTag() {
        return tag;
    }

    @Override
    public int getLength() throws EvaluationException {
        try {
            return getTag().map(t -> Optional.ofNullable((N) t.getTag(key)))
                .orElse(Optional.empty())
                .map(this::getLength)
                .orElse(0);
        } catch (ClassCastException e) {
            return 0;
        }
    }

    @Override
    public V get(int index) throws EvaluationException {
        try {
            if (index < getLength()) {
                return getTag().map(t -> Optional.ofNullable((N) t.getTag(key)))
                    .orElse(Optional.empty())
                    .map(t -> get(t, index))
                    .orElse(null);
            }
        } catch (ClassCastException e) {}
        return null;
    }

    protected abstract int getLength(N tag);

    protected abstract V get(N tag, int index);

    protected abstract N getDefault();

    public static abstract class Factory<L extends ValueTypeListProxyNbtValueListGeneric<N, T, V>, N extends NBTBase, T extends IValueType<V>, V extends IValue>
        extends ValueTypeListProxyNBTFactorySimple<T, V, L> {

        @Override
        public ResourceLocation getName() {
            return new ResourceLocation(Reference.MOD_ID, "nbt.list_value");
        }

        @Override
        protected void serializeNbt(L value, NBTTagCompound tag)
            throws IValueTypeListProxyFactoryTypeRegistry.SerializationException {
            tag.setString("key", value.getKey());
            if (value.getTag()
                .isPresent()) {
                tag.setTag(
                    "tag",
                    value.getTag()
                        .get());
            }
        }

        @Override
        protected L deserializeNbt(NBTTagCompound tag)
            throws IValueTypeListProxyFactoryTypeRegistry.SerializationException {
            return create(tag.getString("key"), Optional.ofNullable(tag.getTag("tag")));
        }

        protected abstract L create(String key, Optional<NBTBase> tag);
    }
}
