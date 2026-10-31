
package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.persist.nbt.INBTProvider;

/**
 * A list proxy for a tank's capacities at a certain position.
 */
public class ValueTypeListProxyPositionedTankCapacities
    extends ValueTypeListProxyPositioned<ValueTypeInteger, ValueTypeInteger.ValueInteger> implements INBTProvider {

    public ValueTypeListProxyPositionedTankCapacities(DimPos pos, ForgeDirection side) {
        super(ValueTypeListProxyFactories.POSITIONED_TANK_CAPACITIES.getName(), ValueTypes.INTEGER, pos, side);
    }

    public ValueTypeListProxyPositionedTankCapacities() {
        this(null, null);
    }

    protected IFluidHandler getTank() {
        return CapabilityHelpers.getCapability(getPos(), CapabilityFluidHandler.FLUID_HANDLER, getSide())
            .getOrNull();
    }

    @Override
    public int getLength() {
        IFluidHandler tank = getTank();
        return tank == null ? 0 : tank.getTanks();
    }

    @Override
    public ValueTypeInteger.ValueInteger get(int index) {
        IFluidHandler tank = getTank();
        if (tank == null || index < 0 || index >= tank.getTanks()) {
            return ValueTypeInteger.ValueInteger.of(0);
        }
        return ValueTypeInteger.ValueInteger.of(tank.getTankCapacity(index));
    }
}
