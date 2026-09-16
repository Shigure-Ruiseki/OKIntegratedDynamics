package ruiseki.integrateddynamics.core.evaluate.variable;

import java.util.Optional;
import java.util.Set;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Sets;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNamed;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNullable;
import ruiseki.okcore.config.IChangedCallback;
import ruiseki.okcore.helper.Helpers;

/**
 * Value type with values that are NBT tags.
 *
 * @author rubensworks
 */
public class ValueTypeNbt extends ValueTypeBase<ValueTypeNbt.ValueNbt>
    implements IValueTypeNullable<ValueTypeNbt.ValueNbt>, IValueTypeNamed<ValueTypeNbt.ValueNbt> {

    private Set<String> tagBlacklist = Sets.newHashSet();

    public ValueTypeNbt() {
        super(
            "nbt",
            Helpers.RGBToInt(0, 170, 170),
            EnumChatFormatting.DARK_AQUA.toString(),
            ValueTypeNbt.ValueNbt.class);
    }

    @Override
    public ValueNbt getDefault() {
        return ValueNbt.of();
    }

    @Override
    public String toCompactString(ValueNbt value) {
        return value.getRawValue()
            .map(NBTBase::toString)
            .orElse("");
    }

    @Override
    public NBTBase serialize(ValueNbt value) {
        NBTTagCompound tag = new NBTTagCompound();
        if (value.getRawValue()
            .isPresent()) {
            tag.setTag(
                "v",
                value.getRawValue()
                    .get());
        }
        return tag;
    }

    @Override
    public ValueNbt deserialize(NBTBase value) {
        if (value instanceof NBTTagCompound compound) {
            if (compound.hasKey("v")) {
                return ValueNbt.of(compound.getTag("v"));
            }
        }
        return ValueNbt.of();
    }

    @Override
    public String toString(ValueNbt value) {
        return value.getRawValue()
            .map(Object::toString)
            .orElse("");
    }

    @Override
    public ValueNbt parseString(String value) throws EvaluationException {
        if (value == null || value.isEmpty()) {
            return ValueNbt.of();
        }
        try {
            return ValueNbt.of((NBTBase) JsonToNBT.func_150315_a(value));
        } catch (NBTException e) {
            throw new EvaluationException(e.getMessage());
        }
    }

    @Override
    public boolean isNull(ValueNbt a) {
        return !a.getRawValue()
            .isPresent();
    }

    /**
     * Filter away the blacklisted tags from the given NBT tag.
     * This won't modify the original tag.
     *
     * @param tag The tag.
     * @return The tag where all blacklisted tags have been removed.
     */
    public NBTBase filterBlacklistedTags(NBTBase tag) {
        if (tag instanceof NBTTagCompound compound) {
            boolean copied = false;
            for (String key : tagBlacklist) {
                if (compound.hasKey(key)) {
                    if (!copied) {
                        copied = true;
                        compound = (NBTTagCompound) compound.copy();
                    }
                    compound.removeTag(key);
                }
            }
            return compound;
        }
        return tag;
    }

    @Override
    public String getName(ValueNbt value) {
        return toCompactString(value);
    }

    public static class ValueNbt extends ValueOptionalBase<NBTBase> {

        private ValueNbt(@Nullable NBTBase value) {
            super(ValueTypes.NBT, value);
        }

        public static ValueNbt of(@Nullable NBTBase value) {
            return new ValueNbt(value);
        }

        public static ValueNbt of(Optional<NBTBase> value) {
            return of(value.orElse(null));
        }

        public static ValueNbt of() {
            return of((NBTBase) null);
        }

        @Override
        protected boolean isEqual(NBTBase a, NBTBase b) {
            return a.equals(b);
        }

        @Override
        public String toString() {
            return getRawValue().map(NBTBase::toString)
                .orElse("none");
        }
    }

    public static class BlacklistChangedCallback implements IChangedCallback {

        @Override
        public void onChanged(Object value) {
            String[] elements = (String[]) value;
            ValueTypes.NBT.tagBlacklist = Sets.newHashSet(elements);
        }

        @Override
        public void onRegisteredPostInit(Object value) {

        }
    }

}
