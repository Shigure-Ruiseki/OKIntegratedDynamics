package ruiseki.integratedtunnels.part;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.integratedtunnels.api.network.IFluidNetwork;
import ruiseki.integratedtunnels.capability.network.FluidNetworkConfig;
import ruiseki.integratedtunnels.core.part.PartTypeInterfacePositionedAddonFiltering;
import ruiseki.integratedtunnels.part.aspect.TunnelAspects;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandler;

/**
 * Interface for filtering fluid handlers.
 * 
 * @author rubensworks
 */
public class PartTypeInterfaceFilteringFluid extends
    PartTypeInterfacePositionedAddonFiltering<IFluidNetwork, IFluidHandler, PartTypeInterfaceFilteringFluid, PartTypeInterfaceFilteringFluid.State> {

    public PartTypeInterfaceFilteringFluid(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(
                this,
                Lists.<IAspect>newArrayList(
                    TunnelAspects.Write.FluidFilter.BOOLEAN_SET_FILTER,
                    TunnelAspects.Write.FluidFilter.FLUIDSTACK_SET_FILTER,
                    TunnelAspects.Write.FluidFilter.LIST_SET_FILTER,
                    TunnelAspects.Write.FluidFilter.PREDICATE_SET_FILTER,
                    TunnelAspects.Write.FluidFilter.NBT_SET_FILTER));
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
    protected PartTypeInterfaceFilteringFluid.State constructDefaultState() {
        return new PartTypeInterfaceFilteringFluid.State(
            Aspects.REGISTRY.getWriteAspects(this)
                .size());
    }

    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.interfaceFluidBaseConsumption;
    }

    public static class State extends
        PartTypeInterfacePositionedAddonFiltering.State<IFluidNetwork, IFluidHandler, PartTypeInterfaceFilteringFluid, PartTypeInterfaceFilteringFluid.State> {

        public State(int inventorySize) {
            super(inventorySize);
        }

        @Override
        public Capability<IFluidHandler> getTargetCapability() {
            return CapabilityFluidHandler.FLUID_HANDLER;
        }

        @Override
        public IFluidHandler getCapabilityInstance() {
            return new PartTypeInterfaceFluid.FluidHandler(this);
        }
    }
}
