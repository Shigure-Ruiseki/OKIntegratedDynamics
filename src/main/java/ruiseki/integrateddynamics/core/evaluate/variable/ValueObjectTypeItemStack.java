package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.registry.GameData;
import joptsimple.internal.Strings;
import lombok.ToString;
import ruiseki.commoncapabilities.api.capability.itemhandler.ItemMatch;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNamed;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNullable;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeUniquelyNamed;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeItemStackLPElement;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeLPElementBase;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Value type with values that are itemstacks.
 *
 * @author rubensworks
 */
public class ValueObjectTypeItemStack extends ValueObjectTypeBase<ValueObjectTypeItemStack.ValueItemStack>
    implements IValueTypeNamed<ValueObjectTypeItemStack.ValueItemStack>,
    IValueTypeUniquelyNamed<ValueObjectTypeItemStack.ValueItemStack>,
    IValueTypeNullable<ValueObjectTypeItemStack.ValueItemStack> {

    public ValueObjectTypeItemStack() {
        super("itemstack", ValueObjectTypeItemStack.ValueItemStack.class);
    }

    public static String getItemStackDisplayNameUsSafe(ItemStack itemStack) throws NoSuchMethodException {
        return !ItemHelpers.isEmpty(itemStack)
            ? (itemStack.getDisplayName() + (itemStack.stackSize > 1 ? " (" + itemStack.stackSize + ")" : ""))
            : "";
    }

    public static String getItemStackDisplayNameSafe(ItemStack itemStack) {
        if (ItemHelpers.isEmpty(itemStack)) {
            return "";
        }
        // Certain mods may call client-side only methods,
        // so call a server-side-safe fallback method if that fails.
        try {
            return getItemStackDisplayNameUsSafe(itemStack);
        } catch (NoSuchMethodException e) {
            return LangHelpers.localize(itemStack.getUnlocalizedName() + ".name");
        }
    }

    @Override
    public ValueItemStack getDefault() {
        return ValueItemStack.of(ItemHelpers.EMPTY);
    }

    @Override
    public String toCompactString(ValueItemStack value) {
        ItemStack itemStack = value.getRawValue();
        if (!ItemHelpers.isEmpty(itemStack)) {
            return ValueObjectTypeItemStack.getItemStackDisplayNameSafe(itemStack);
        }
        return "";
    }

    @Override
    public String serialize(ValueItemStack value) {
        ItemStack itemStack = value.getRawValue();
        if (ItemHelpers.isEmpty(itemStack)) {
            return "";
        }
        NBTTagCompound tag = new NBTTagCompound();
        itemStack.writeToNBT(tag);
        tag.setInteger("Count", itemStack.stackSize);
        return tag.toString();
    }

    @Override
    public ValueItemStack deserialize(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return ValueItemStack.of(ItemHelpers.EMPTY);
        }
        try {
            NBTTagCompound tag = (NBTTagCompound) JsonToNBT.func_150315_a(value);
            // Forge returns air for tags with negative count,
            // so we set it to 1 for deserialization and fix it afterwards.
            int realCount = tag.getInteger("Count");
            // Consider the tag immutable, to avoid changes elsewhere
            tag = (NBTTagCompound) tag.copy();
            tag.setByte("Count", (byte) 1);
            ItemStack itemStack = ItemStack.loadItemStackFromNBT(tag);
            if (!ItemHelpers.isEmpty(itemStack)) {
                itemStack.stackSize = realCount;
            }
            return ValueItemStack.of(itemStack);
        } catch (NBTException e) {
            return ValueItemStack.of(ItemHelpers.EMPTY);
        }
    }

    @Override
    public String getName(ValueItemStack a) {
        return toCompactString(a);
    }

    @Override
    public boolean isNull(ValueItemStack a) {
        return ItemHelpers.isEmpty(a.getRawValue());
    }

    @Override
    public ValueTypeLPElementBase createLogicProgrammerElement() {
        return new ValueTypeItemStackLPElement<>(
            this,
            new ValueTypeItemStackLPElement.IItemStackToValue<ValueObjectTypeItemStack.ValueItemStack>() {

                @Override
                public boolean isNullable() {
                    return true;
                }

                @Override
                public LangHelpers.UnlocalizedString validate(ItemStack itemStack) {
                    return null;
                }

                @Override
                public ValueObjectTypeItemStack.ValueItemStack getValue(ItemStack itemStack) {
                    return ValueObjectTypeItemStack.ValueItemStack.of(itemStack);
                }

                @Override
                public ItemStack getValueAsItemStack(ValueItemStack value) {
                    return value.getRawValue();
                }
            });
    }

    @Override
    public String getUniqueName(ValueItemStack value) {
        ItemStack itemStack = value.getRawValue();
        if (!ItemHelpers.isEmpty(itemStack) && itemStack.getItem() != null) {
            return GameData.getItemRegistry()
                .getNameForObject(itemStack.getItem())
                + (itemStack.getItemDamage() > 0 ? " " + itemStack.getItemDamage() : "");
        }
        return "";
    }

    @ToString
    public static class ValueItemStack extends ValueBase {

        private final ItemStack itemStack;

        private ValueItemStack(ItemStack itemStack) {
            super(ValueTypes.OBJECT_ITEMSTACK);
            this.itemStack = ItemHelpers.copy(itemStack);
        }

        public static ValueItemStack of(ItemStack itemStack) {
            return new ValueItemStack(itemStack);
        }

        public ItemStack getRawValue() {
            return this.itemStack;
        }

        @Override
        public boolean equals(Object o) {

            if (this == o) return true;
            if (!(o instanceof ValueItemStack)) return false;
            ValueItemStack that = (ValueItemStack) o;

            ItemStack a = this.getRawValue();
            ItemStack b = that.getRawValue();

            if (ItemHelpers.isEmpty(a) && ItemHelpers.isEmpty(b)) {
                return true;
            }
            if (ItemHelpers.isEmpty(a) || ItemHelpers.isEmpty(b)) {
                return false;
            }
            return ItemMatch.areItemStacksEqual(a, b, ItemMatch.EXACT);
        }

        @Override
        public int hashCode() {
            return 37 + (!ItemHelpers.isEmpty(getRawValue()) ? ItemHelpers.getItemStackHashCode(getRawValue()) : 0);
        }
    }
}
