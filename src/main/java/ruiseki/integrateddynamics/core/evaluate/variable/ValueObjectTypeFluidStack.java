package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

import lombok.ToString;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNamed;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNullable;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeUniquelyNamed;
import ruiseki.integrateddynamics.core.helper.Helpers;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeItemStackLPElement;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeLPElementBase;
import ruiseki.okcore.helper.FluidHelpers;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Value type with values that are fluidstacks.
 *
 * @author rubensworks
 */
public class ValueObjectTypeFluidStack extends ValueObjectTypeBase<ValueObjectTypeFluidStack.ValueFluidStack>
    implements IValueTypeNamed<ValueObjectTypeFluidStack.ValueFluidStack>,
    IValueTypeUniquelyNamed<ValueObjectTypeFluidStack.ValueFluidStack>,
    IValueTypeNullable<ValueObjectTypeFluidStack.ValueFluidStack> {

    public ValueObjectTypeFluidStack() {
        super("fluidstack", ValueObjectTypeFluidStack.ValueFluidStack.class);
    }

    @Override
    public ValueFluidStack getDefault() {
        return ValueFluidStack.of(FluidHelpers.EMPTY);
    }

    @Override
    public String toCompactString(ValueFluidStack value) {
        FluidStack fluidStack = value.getRawValue();
        return !FluidHelpers.isEmpty(fluidStack)
            ? String.format("%s (%s mB)", fluidStack.getLocalizedName(), fluidStack.amount)
            : "";
    }

    @Override
    public String serialize(ValueFluidStack value) {
        NBTTagCompound tag = new NBTTagCompound();
        FluidStack fluidStack = value.getRawValue();
        if (!FluidHelpers.isEmpty(fluidStack)) {
            fluidStack.writeToNBT(tag);
        }
        return tag.toString();
    }

    @Override
    public ValueFluidStack deserialize(String value) {
        try {
            NBTTagCompound tag = (NBTTagCompound) JsonToNBT.func_150315_a(value);
            FluidStack fluidStack = FluidStack.loadFluidStackFromNBT(tag);
            return ValueFluidStack.of(fluidStack);
        } catch (NBTException e) {
            return null;
        }
    }

    @Override
    public String getName(ValueFluidStack a) {
        return toCompactString(a);
    }

    @Override
    public boolean isNull(ValueFluidStack a) {
        return a == null || FluidHelpers.isEmpty(a.getRawValue());
    }

    @Override
    public ValueTypeLPElementBase createLogicProgrammerElement() {
        return new ValueTypeItemStackLPElement<>(
            this,
            new ValueTypeItemStackLPElement.IItemStackToValue<ValueObjectTypeFluidStack.ValueFluidStack>() {

                @Override
                public boolean isNullable() {
                    return true;
                }

                @Override
                public LangHelpers.UnlocalizedString validate(ItemStack itemStack) {
                    return ItemHelpers.isEmpty(itemStack) || FluidHelpers.getFluidHandler(itemStack)
                        .isPresent()
                        || (itemStack.getItem() instanceof ItemBlock blockItem && blockItem instanceof IFluidBlock)
                            ? null
                            : new LangHelpers.UnlocalizedString(L10NValues.VALUETYPE_OBJECT_FLUID_ERROR_NOFLUID);
                }

                @Override
                public ValueObjectTypeFluidStack.ValueFluidStack getValue(ItemStack itemStack) {
                    return ValueObjectTypeFluidStack.ValueFluidStack.of(Helpers.getFluidStack(itemStack));
                }

                @Override
                public ItemStack getValueAsItemStack(ValueFluidStack value) {
                    if (value == null || FluidHelpers.isEmpty(value.getRawValue())) {
                        return ItemHelpers.EMPTY;
                    }

                    FluidStack fluidStack = value.getRawValue();
                    return Helpers.getItemStackFromFluid(fluidStack);
                }
            });
    }

    @Override
    public String getUniqueName(ValueFluidStack value) {
        FluidStack fluidStack = value.getRawValue();
        return !FluidHelpers.isEmpty(fluidStack) ? String.format(
            "%s %s",
            fluidStack.getFluid()
                .getName(),
            fluidStack.amount) : "";
    }

    @ToString
    public static class ValueFluidStack extends ValueBase {

        private final FluidStack fluidStack;

        private ValueFluidStack(FluidStack fluidStack) {
            super(ValueTypes.OBJECT_FLUIDSTACK);
            this.fluidStack = fluidStack;
        }

        public static ValueFluidStack of(FluidStack fluidStack) {
            return new ValueFluidStack(fluidStack);
        }

        public FluidStack getRawValue() {
            return fluidStack;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ValueFluidStack
                && FluidStack.areFluidStackTagsEqual(this.getRawValue(), ((ValueFluidStack) o).getRawValue());
        }

        @Override
        public int hashCode() {
            return fluidStack != null ? fluidStack.hashCode() : 0;
        }
    }
}
