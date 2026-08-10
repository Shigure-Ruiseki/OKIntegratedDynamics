package ruiseki.integrateddynamics.core.evaluate.variable;

import java.util.Objects;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.registry.GameData;
import lombok.ToString;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNamed;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNullable;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeUniquelyNamed;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeItemStackLPElement;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeLPElementBase;
import ruiseki.okcore.helper.ItemStackHelpers;
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
        super("itemstack");
    }

    @Override
    public ValueItemStack getDefault() {
        return ValueItemStack.of(null);
    }

    @Override
    public String toCompactString(ValueItemStack value) {
        ItemStack itemStack = value.getRawValue();
        return itemStack != null ? itemStack.getDisplayName() : "";
    }

    @Override
    public String serialize(ValueItemStack value) {
        NBTTagCompound tag = new NBTTagCompound();
        ItemStack itemStack = value.getRawValue();
        if (itemStack != null) {
            itemStack.writeToNBT(tag);
            tag.setInteger("Count", itemStack.stackSize);
        }
        return tag.toString();
    }

    @Override
    public ValueItemStack deserialize(String value) {
        try {
            NBTTagCompound tag = (NBTTagCompound) JsonToNBT.func_150315_a(value);
            ItemStack itemStack = ItemStack.loadItemStackFromNBT(tag);
            if (itemStack != null) {
                itemStack.stackSize = tag.getInteger("Count");
            }
            return ValueItemStack.of(itemStack);
        } catch (NBTException e) {
            return null;
        }
    }

    @Override
    public String getName(ValueItemStack a) {
        return toCompactString(a);
    }

    @Override
    public boolean isNull(ValueItemStack a) {
        return a.getRawValue() == null;
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
            });
    }

    @Override
    public String getUniqueName(ValueItemStack value) {
        ItemStack itemStack = value.getRawValue();
        return itemStack != null ? GameData.getItemRegistry()
            .getNameForObject(itemStack.getItem())
            + (itemStack.getItemDamage() > 0 ? " " + itemStack.getItemDamage() : "") : "";
    }

    @ToString
    public static class ValueItemStack extends ValueBase {

        private final ItemStack itemStack;

        private ValueItemStack(ItemStack itemStack) {
            super(ValueTypes.OBJECT_ITEMSTACK);
            this.itemStack = Objects
                .requireNonNull(itemStack, "Attempted to create a ValueItemStack for a null ItemStack.");
        }

        public static ValueItemStack of(ItemStack itemStack) {
            return new ValueItemStack(itemStack);
        }

        public ItemStack getRawValue() {
            return itemStack;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ValueItemStack
                && ItemStack.areItemStacksEqual(((ValueItemStack) o).itemStack, this.itemStack);
        }

        @Override
        public int hashCode() {
            return 37 + ItemStackHelpers.getItemStackHashCode(itemStack);
        }
    }
}
