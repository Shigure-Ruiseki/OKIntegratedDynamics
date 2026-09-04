package ruiseki.integratedtunnels.part;

import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.network.PositionedAddonsNetworkIngredientsFilter;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;
import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.integratedtunnels.api.network.IFluidNetwork;
import ruiseki.integratedtunnels.core.TunnelHelpers;
import ruiseki.integratedtunnels.core.part.PartStatePositionedAddon;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidTankProperties;
import ruiseki.okcore.helper.FluidHelpers;

/**
 * A part state for handling fluid import and export.
 * It also acts as an fluid capability that can be added to itself.
 *
 * @author rubensworks
 */
public class PartStateFluid<P extends IPartTypeWriter> extends PartStatePositionedAddon<P, IFluidNetwork, FluidStack>
    implements IFluidHandler {

    public PartStateFluid(int inventorySize, boolean canReceive, boolean canExtract) {
        super(inventorySize, canReceive, canExtract);
    }

    @Override
    public <T2> LazyOptional<T2> getCapability(Capability<T2> capability, INetwork network, IPartNetwork partNetwork,
        PartTarget target) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER) {
            return LazyOptional.of(() -> this)
                .cast();
        }
        return super.getCapability(capability, network, partNetwork, target);
    }

    protected IFluidHandler getFluidHandler() {
        return getPositionedAddonsNetwork()
            .getChannelExternal(CapabilityFluidHandler.FLUID_HANDLER, TunnelHelpers.getPassiveInteractionChannel(this));
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return getPositionedAddonsNetwork() != null && getStorageFilter() != null
            ? getFluidHandler().getTankProperties()
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
            && getStorageFilter() != null
            && getStorageFilter().testInsertion(resource) ? getFluidHandler().fill(rateLimitFluid(resource), doFill)
                : 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return canExtract() && getPositionedAddonsNetwork() != null
            && getStorageFilter() != null
            && getStorageFilter().testExtraction(resource) ? getFluidHandler().drain(rateLimitFluid(resource), doDrain)
                : FluidHelpers.EMPTY;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (canExtract() && getPositionedAddonsNetwork() != null && getStorageFilter() != null) {
            PositionedAddonsNetworkIngredientsFilter<FluidStack> filter = getStorageFilter();

            // If we do an effective extraction, first simulate to check if it matches the filter
            if (doDrain) {
                FluidStack drainedSimulated = getFluidHandler()
                    .drain(Math.min(maxDrain, GeneralConfig.fluidRateLimit), true);
                if (!filter.testExtraction(drainedSimulated)) {
                    return FluidHelpers.EMPTY;
                }
            }

            FluidStack drained = getFluidHandler().drain(Math.min(maxDrain, GeneralConfig.fluidRateLimit), doDrain);

            // If simulating, just check the output
            if (!doDrain && !filter.testExtraction(drained)) {
                return FluidHelpers.EMPTY;
            }

            return drained;
        }
        return FluidHelpers.EMPTY;
    }
}
