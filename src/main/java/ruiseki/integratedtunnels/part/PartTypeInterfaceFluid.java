package ruiseki.integratedtunnels.part;

import net.minecraftforge.fluids.FluidStack;

import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.integratedtunnels.api.network.IFluidNetwork;
import ruiseki.integratedtunnels.capability.network.FluidNetworkConfig;
import ruiseki.integratedtunnels.core.part.IPartTypeInterfacePositionedAddon;
import ruiseki.integratedtunnels.core.part.PartTypeInterfacePositionedAddon;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidTankProperties;
import ruiseki.okcore.helper.FluidHelpers;

/**
 * Interface for fluid handlers.
 * 
 * @author rubensworks
 */
public class PartTypeInterfaceFluid extends
    PartTypeInterfacePositionedAddon<IFluidNetwork, IFluidHandler, PartTypeInterfaceFluid, PartTypeInterfaceFluid.State> {

    public PartTypeInterfaceFluid(String name) {
        super(name);
    }

    @Override
    public Capability<IFluidNetwork> getNetworkCapability() {
        return FluidNetworkConfig.CAPABILITY;
    }

    @Override
    public Capability<IFluidHandler> getTargetCapability() {
        return CapabilityFluidHandler.FLUID_HANDLER;
    }

    @Override
    protected PartTypeInterfaceFluid.State constructDefaultState() {
        return new PartTypeInterfaceFluid.State();
    }

    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.interfaceFluidBaseConsumption;
    }

    public static class State extends
        PartTypeInterfacePositionedAddon.State<IFluidNetwork, IFluidHandler, PartTypeInterfaceFluid, PartTypeInterfaceFluid.State> {

        @Override
        public Capability<IFluidHandler> getTargetCapability() {
            return CapabilityFluidHandler.FLUID_HANDLER;
        }

        @Override
        public IFluidHandler getCapabilityInstance() {
            return new PartTypeInterfaceFluid.FluidHandler(this);
        }
    }

    public static class FluidHandler implements IFluidHandler {

        private final IPartTypeInterfacePositionedAddon.IState<IFluidNetwork, IFluidHandler, ?, ?> state;

        public FluidHandler(IState<IFluidNetwork, IFluidHandler, ?, ?> state) {
            this.state = state;
        }

        protected IFluidHandler getFluidHandler() {
            return state.getPositionedAddonsNetwork()
                .getChannelExternal(CapabilityFluidHandler.FLUID_HANDLER, state.getChannel());
        }

        @Override
        public IFluidTankProperties[] getTankProperties() {
            if (!state.isNetworkAndPositionValid()) {
                return null;
            }
            state.disablePosition();
            IFluidTankProperties[] ret = getFluidHandler().getTankProperties();
            state.enablePosition();
            return ret;
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            if (!state.isNetworkAndPositionValid()) {
                return 0;
            }
            state.disablePosition();
            int ret = getFluidHandler().fill(resource, doFill);
            state.enablePosition();
            return ret;
        }

        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            if (!state.isNetworkAndPositionValid()) {
                return FluidHelpers.EMPTY;
            }
            state.disablePosition();
            FluidStack ret = getFluidHandler().drain(resource, doDrain);
            state.enablePosition();
            return ret;
        }

        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            if (!state.isNetworkAndPositionValid()) {
                return FluidHelpers.EMPTY;
            }
            state.disablePosition();
            FluidStack ret = getFluidHandler().drain(maxDrain, doDrain);
            state.enablePosition();
            return ret;
        }
    }
}
