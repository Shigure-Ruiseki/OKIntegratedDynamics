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
 * A list proxy for a tank's fluidstacks at a certain position.
 */
public class ValueTypeListProxyPositionedTankFluidStacks
    extends ValueTypeListProxyBase<ValueObjectTypeFluidStack, ValueObjectTypeFluidStack.ValueFluidStack>
    implements INBTProvider {

    @NBTPersist
    private DimPos pos;
    @NBTPersist
    private ForgeDirection side;

    public ValueTypeListProxyPositionedTankFluidStacks() {
        this(null, null);
    }

    public ValueTypeListProxyPositionedTankFluidStacks(DimPos pos, ForgeDirection side) {
        super(ValueTypeListProxyFactories.POSITIONED_TANK_FLUIDSTACKS.getName(), ValueTypes.OBJECT_FLUIDSTACK);
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
    public ValueObjectTypeFluidStack.ValueFluidStack get(int index) {
        return ValueObjectTypeFluidStack.ValueFluidStack.of(getTank().getTankProperties()[index].getContents());
    }

    @Override
    public void writeGeneratedFieldsToNBT(NBTTagCompound tag) {

    }

    @Override
    public void readGeneratedFieldsFromNBT(NBTTagCompound tag) {

    }
}
