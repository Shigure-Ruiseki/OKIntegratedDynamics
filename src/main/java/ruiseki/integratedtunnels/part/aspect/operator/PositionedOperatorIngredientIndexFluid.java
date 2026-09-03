package ruiseki.integratedtunnels.part.aspect.operator;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeFluidStack;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeLong;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integratedtunnels.capability.network.FluidNetworkConfig;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.DimPos;

/**
 * @author rubensworks
 */
public class PositionedOperatorIngredientIndexFluid extends PositionedOperatorIngredientIndex<FluidStack, Integer> {

    public PositionedOperatorIngredientIndexFluid(DimPos pos, ForgeDirection side, int channel) {
        super("countbyfluid", new Function(), ValueTypes.OBJECT_FLUIDSTACK, ValueTypes.LONG, pos, side, channel);
    }

    @Override
    protected Capability<? extends IPositionedAddonsNetworkIngredients<FluidStack, Integer>> getNetworkCapability() {
        return FluidNetworkConfig.CAPABILITY;
    }

    public static class Function extends PositionedOperatorIngredientIndex.Function<FluidStack, Integer> {

        @Override
        public IValue evaluate(SafeVariablesGetter variables) throws EvaluationException {
            ValueObjectTypeFluidStack.ValueFluidStack fluidStack = variables.getValue(0, ValueTypes.OBJECT_FLUIDSTACK);
            return ValueTypeLong.ValueLong.of(
                getOperator().getChannelIndex()
                    .map(index -> index.getQuantity(fluidStack.getRawValue()))
                    .orElse(0L));
        }
    }
}
