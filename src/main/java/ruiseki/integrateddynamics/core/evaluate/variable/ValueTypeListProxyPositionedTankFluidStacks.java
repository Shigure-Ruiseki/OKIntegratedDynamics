package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.FluidHelpers;
import ruiseki.okcore.persist.nbt.INBTProvider;

/**
 * A list proxy for a tank's fluidstacks at a certain position.
 */
public class ValueTypeListProxyPositionedTankFluidStacks
    extends ValueTypeListProxyPositioned<ValueObjectTypeFluidStack, ValueObjectTypeFluidStack.ValueFluidStack>
    implements INBTProvider {

    public ValueTypeListProxyPositionedTankFluidStacks(DimPos pos, ForgeDirection side) {
        super(
            ValueTypeListProxyFactories.POSITIONED_TANK_FLUIDSTACKS.getName(),
            ValueTypes.OBJECT_FLUIDSTACK,
            pos,
            side);
    }

    public ValueTypeListProxyPositionedTankFluidStacks() {
        this(null, null);
    }

    protected LazyOptional<IFluidHandler> getTank() {
        return CapabilityHelpers.getCapability(getPos(), CapabilityFluidHandler.FLUID_HANDLER, getSide());
    }

    @Override
    public int getLength() {
        return getTank().map(IFluidHandler::getTanks)
            .orElse(0);
    }

    @Override
    public ValueObjectTypeFluidStack.ValueFluidStack get(int index) {
        return ValueObjectTypeFluidStack.ValueFluidStack.of(
            getTank().map(
                fluidHandler -> fluidHandler.getFluidInTank(index)
                    .copy())
                .orElse(FluidHelpers.EMPTY));
    }
}
