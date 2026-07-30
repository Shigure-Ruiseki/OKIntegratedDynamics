package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.TileHelpers;
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
        return TileHelpers.getSafeTile(pos.getWorld(), pos.getBlockPos(), IFluidHandler.class);
    }

    @Override
    public int getLength() {
        IFluidHandler tank = getTank();
        if (tank == null) {
            return 0;
        }
        FluidTankInfo[] tanks = tank.getTankInfo(side);
        if (tanks == null) {
            return 0;
        }
        return tanks.length;
    }

    @Override
    public ValueTypeInteger.ValueInteger get(int index) {
        return ValueTypeInteger.ValueInteger.of(getTank().getTankInfo(side)[index].capacity);
    }

    @Override
    public void writeGeneratedFieldsToNBT(NBTTagCompound tag) {

    }

    @Override
    public void readGeneratedFieldsFromNBT(NBTTagCompound tag) {

    }
}
