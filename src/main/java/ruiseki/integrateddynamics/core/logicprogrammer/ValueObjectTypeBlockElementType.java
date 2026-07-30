package ruiseki.integrateddynamics.core.logicprogrammer;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeBlock;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.okcore.helper.BlockHelpers;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Logic programmer element types for the block value type.
 * 
 * @author rubensworks
 */
public class ValueObjectTypeBlockElementType extends SingleElementType<ValueTypeItemStackElement> {

    public ValueObjectTypeBlockElementType() {
        super(new SingleElementType.ILogicProgrammerElementConstructor<ValueTypeItemStackElement>() {

            @Override
            public ValueTypeItemStackElement construct() {
                return new ValueTypeItemStackElement<>(
                    ValueTypes.OBJECT_BLOCK,
                    new ValueTypeItemStackElement.IItemStackToValue<ValueObjectTypeBlock.ValueBlock>() {

                        @Override
                        public LangHelpers.UnlocalizedString validate(ItemStack itemStack) {
                            if (!(itemStack.getItem() instanceof ItemBlock)) {
                                return new LangHelpers.UnlocalizedString(
                                    L10NValues.VALUETYPE_OBJECT_BLOCK_ERROR_NOBLOCK);
                            }
                            return null;
                        }

                        @Override
                        public ValueObjectTypeBlock.ValueBlock getValue(ItemStack itemStack) {
                            return ValueObjectTypeBlock.ValueBlock
                                .of(BlockHelpers.getBlockStateFromItemStack(itemStack));
                        }
                    },
                    LogicProgrammerElementTypes.OBJECT_BLOCK_TYPE);
            }
        }, "block");
    }
}
