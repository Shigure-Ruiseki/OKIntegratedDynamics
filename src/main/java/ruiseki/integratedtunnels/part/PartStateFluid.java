package ruiseki.integratedtunnels.part;

import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;
import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.integratedtunnels.api.network.IFluidNetwork;
import ruiseki.integratedtunnels.core.part.PartStatePositionedAddon;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidTankProperties;

/**
 * A part state for handling fluid import and export.
 * It also acts as an fluid capability that can be added to itself.
 * 
 * @author rubensworks
 */
public class PartStateFluid<P extends IPartTypeWriter> extends PartStatePositionedAddon<P, IFluidNetwork>
    implements IFluidHandler {

    public PartStateFluid(int inventorySize, boolean canReceive, boolean canExtract) {
        super(inventorySize, canReceive, canExtract);
    }

    protected IFluidHandler getFluidHandler() {
        return getPositionedAddonsNetwork().getChannelExternal(CapabilityFluidHandler.FLUID_HANDLER, getChannel());
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return getPositionedAddonsNetwork() != null ? getFluidHandler().getTankProperties()
            : new IFluidTankProperties[0];
    }

    protected FluidStack rateLimitFluid(FluidStack fluidStack) {
        if (fluidStack != null && fluidStack.amount > GeneralConfig.fluidRateLimit) {
            return new FluidStack(fluidStack, GeneralConfig.fluidRateLimit);
        }
        return fluidStack;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return canReceive() && getPositionedAddonsNetwork() != null
            ? getFluidHandler().fill(rateLimitFluid(resource), doFill)
            : 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return canExtract() && getPositionedAddonsNetwork() != null
            ? getFluidHandler().drain(rateLimitFluid(resource), doDrain)
            : null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return canExtract() && getPositionedAddonsNetwork() != null
            ? getFluidHandler().drain(Math.min(maxDrain, GeneralConfig.fluidRateLimit), doDrain)
            : null;
    }
}
