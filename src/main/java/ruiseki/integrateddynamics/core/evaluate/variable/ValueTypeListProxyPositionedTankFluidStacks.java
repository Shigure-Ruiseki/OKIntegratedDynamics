package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidTankProperties;
import ruiseki.okcore.helper.CapabilityHelpers;
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

    protected IFluidHandler getTank() {
        return CapabilityHelpers
            .getCapability(getPos().getWorld(), getPos().getBlockPos(), CapabilityFluidHandler.FLUID_HANDLER, getSide())
            .getOrNull();
    }

    @Override
    public int getLength() {
        IFluidHandler tank = getTank();
        if (tank == null) {
            return 0;
        }
        IFluidTankProperties[] tanks = tank.getTankProperties();
        if (tanks == null) {
            return 0;
        }
        return tanks.length;
    }

    @Override
    public ValueObjectTypeFluidStack.ValueFluidStack get(int index) {
        return ValueObjectTypeFluidStack.ValueFluidStack.of(getTank().getTankProperties()[index].getContents());
    }
}
