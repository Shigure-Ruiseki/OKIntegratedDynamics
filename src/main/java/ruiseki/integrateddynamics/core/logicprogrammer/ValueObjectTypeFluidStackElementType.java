package ruiseki.integrateddynamics.core.logicprogrammer;

import net.minecraft.item.ItemStack;

import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeFluidStack;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.Helpers;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Logic programmer element types for the fluidstack value type.
 * 
 * @author rubensworks
 */
public class ValueObjectTypeFluidStackElementType extends SingleElementType<ValueTypeItemStackElement> {

    public ValueObjectTypeFluidStackElementType() {
        super(new ILogicProgrammerElementConstructor<ValueTypeItemStackElement>() {

            @Override
            public ValueTypeItemStackElement construct() {
                return new ValueTypeItemStackElement<>(
                    ValueTypes.OBJECT_FLUIDSTACK,
                    new ValueTypeItemStackElement.IItemStackToValue<ValueObjectTypeFluidStack.ValueFluidStack>() {

                        @Override
                        public LangHelpers.UnlocalizedString validate(ItemStack itemStack) {
                            return Helpers.getFluidStack(itemStack) != null ? null
                                : new LangHelpers.UnlocalizedString(L10NValues.VALUETYPE_OBJECT_FLUID_ERROR_NOFLUID);
                        }

                        @Override
                        public ValueObjectTypeFluidStack.ValueFluidStack getValue(ItemStack itemStack) {
                            return ValueObjectTypeFluidStack.ValueFluidStack.of(Helpers.getFluidStack(itemStack));
                        }
                    },
                    LogicProgrammerElementTypes.OBJECT_FLUIDSTACK_TYPE);
            }
        }, "fluidstack");
    }
}
