package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidTankProperties;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.persist.nbt.INBTProvider;
import ruiseki.okcore.persist.nbt.NBTPersist;

/**
 * A list proxy for a tank's capacities at a certain position.
 */
public class ValueTypeListProxyPositionedTankCapacities
    extends ValueTypeListProxyBase<ValueTypeInteger, ValueTypeInteger.ValueInteger> implements INBTProvider {

    @NBTPersist
    private DimPos pos;
    @NBTPersist
    private ForgeDirection side;

    public ValueTypeListProxyPositionedTankCapacities() {
        this(null, null);
    }

    public ValueTypeListProxyPositionedTankCapacities(DimPos pos, ForgeDirection side) {
        super(ValueTypeListProxyFactories.POSITIONED_TANK_CAPACITIES.getName(), ValueTypes.INTEGER);
        this.pos = pos;
        this.side = side;
    }

    protected IFluidHandler getTank() {
        return CapabilityHelpers
            .getCapability(pos.getWorld(), pos.getBlockPos(), CapabilityFluidHandler.FLUID_HANDLER, side)
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
    public ValueTypeInteger.ValueInteger get(int index) {
        return ValueTypeInteger.ValueInteger.of(getTank().getTankProperties()[index].getCapacity());
    }

    @Override
    public void writeGeneratedFieldsToNBT(NBTTagCompound tag) {

    }

    @Override
    public void readGeneratedFieldsFromNBT(NBTTagCompound tag) {

    }
}
