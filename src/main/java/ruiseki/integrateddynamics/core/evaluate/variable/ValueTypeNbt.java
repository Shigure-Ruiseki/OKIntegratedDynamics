package ruiseki.integrateddynamics.core.evaluate.variable;

import java.util.Set;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Sets;

import lombok.ToString;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNullable;
import ruiseki.okcore.config.IChangedCallback;
import ruiseki.okcore.helper.Helpers;

/**
 * Value type with values that are NBT tags.
 *
 * @author rubensworks
 */
public class ValueTypeNbt extends ValueTypeBase<ValueTypeNbt.ValueNbt>
    implements IValueTypeNullable<ValueTypeNbt.ValueNbt> {

    private Set<String> tagBlacklist = Sets.newHashSet();

    public ValueTypeNbt() {
        super("nbt", Helpers.RGBToInt(0, 170, 170), EnumChatFormatting.DARK_AQUA.toString());
    }

    @Override
    public ValueNbt getDefault() {
        return ValueNbt.of(new NBTTagCompound());
    }

    @Override
    public String toCompactString(ValueNbt value) {
        return value.getRawValue()
            .toString();
    }

    @Override
    public String serialize(ValueNbt value) {
        return isNull(value) ? "{}"
            : value.getRawValue()
                .toString();
    }

    @Override
    public ValueNbt deserialize(String value) {
        try {
            return ValueNbt.of((NBTTagCompound) JsonToNBT.func_150315_a(value));
        } catch (NBTException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public boolean isNull(ValueNbt a) {
        return a.getRawValue()
            .hasNoTags();
    }

    /**
     * Filter away the blacklisted tags from the given NBT tag.
     * This won't modify the original tag.
     * 
     * @param tag The tag.
     * @return The tag where all blacklisted tags have been removed.
     */
    public NBTTagCompound filterBlacklistedTags(NBTTagCompound tag) {
        boolean copied = false;
        for (String key : tagBlacklist) {
            if (tag.hasKey(key)) {
                if (!copied) {
                    copied = true;
                    tag = (NBTTagCompound) tag.copy();
                }
                tag.removeTag(key);
            }
        }
        return tag;
    }

    @ToString
    public static class ValueNbt extends ValueBase {

        private final NBTTagCompound value;

        private ValueNbt(NBTTagCompound value) {
            super(ValueTypes.NBT);
            this.value = ValueTypes.NBT.filterBlacklistedTags(value);
        }

        public static ValueNbt of(@Nullable NBTTagCompound value) {
            return value == null ? ValueTypes.NBT.getDefault() : new ValueNbt(value);
        }

        public NBTTagCompound getRawValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ValueNbt && ((ValueNbt) o).value.equals(this.value);
        }

        @Override
        public int hashCode() {
            return getType().hashCode() + value.hashCode();
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
