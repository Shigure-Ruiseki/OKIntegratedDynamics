package ruiseki.integratedtunnels.part.aspect.operator;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeItemStack;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeLong;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integratedtunnels.capability.network.ItemNetworkConfig;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.DimPos;

/**
 * @author rubensworks
 */
public class PositionedOperatorIngredientIndexItem extends PositionedOperatorIngredientIndex<ItemStack, Integer> {

    public PositionedOperatorIngredientIndexItem(DimPos pos, ForgeDirection side, int channel) {
        super("countbyitem", new Function(), ValueTypes.OBJECT_ITEMSTACK, ValueTypes.LONG, pos, side, channel);
    }

    @Override
    protected Capability<? extends IPositionedAddonsNetworkIngredients<ItemStack, Integer>> getNetworkCapability() {
        return ItemNetworkConfig.CAPABILITY;
    }

    public static class Function extends PositionedOperatorIngredientIndex.Function<ItemStack, Integer> {

        @Override
        public IValue evaluate(SafeVariablesGetter variables) throws EvaluationException {
            ValueObjectTypeItemStack.ValueItemStack itemStack = variables.getValue(0, ValueTypes.OBJECT_ITEMSTACK);
            return ValueTypeLong.ValueLong.of(
                getOperator().getChannelIndex()
                    .map(index -> index.getQuantity(itemStack.getRawValue()))
                    .orElse(0L));
        }
    }
}
