package ruiseki.integrateddynamics.core.logicprogrammer;

import net.minecraft.item.ItemStack;

import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Logic programmer element types for the itemstack value type.
 * 
 * @author rubensworks
 */
public class ValueObjectTypeItemStackElementType extends SingleElementType<ValueTypeItemStackElement> {

    public ValueObjectTypeItemStackElementType() {
        super(new SingleElementType.ILogicProgrammerElementConstructor<ValueTypeItemStackElement>() {

            @Override
            public ValueTypeItemStackElement construct() {
                return new ValueTypeItemStackElement<>(
                    ValueTypes.OBJECT_ITEMSTACK,
                    new ValueTypeItemStackElement.IItemStackToValue<ValueObjectTypeItemStack.ValueItemStack>() {

                        @Override
                        public LangHelpers.UnlocalizedString validate(ItemStack itemStack) {
                            return null;
                        }

                        @Override
                        public ValueObjectTypeItemStack.ValueItemStack getValue(ItemStack itemStack) {
                            return ValueObjectTypeItemStack.ValueItemStack.of(itemStack);
                        }
                    },
                    LogicProgrammerElementTypes.OBJECT_ITEMSTACK_TYPE);
            }
        }, "itemstack");
    }
}
