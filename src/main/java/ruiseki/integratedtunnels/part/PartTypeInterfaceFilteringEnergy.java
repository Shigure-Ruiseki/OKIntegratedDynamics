package ruiseki.integratedtunnels.part;

import com.google.common.collect.Lists;

import cofh.api.energy.IEnergyStorage;
import ruiseki.integrateddynamics.api.network.IEnergyNetwork;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.capability.network.EnergyNetworkConfig;
import ruiseki.integrateddynamics.core.helper.EnergyHelpers;
import ruiseki.integrateddynamics.core.part.aspect.AspectRegistry;
import ruiseki.integrateddynamics.part.aspect.Aspects;
import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.integratedtunnels.core.part.PartTypeInterfacePositionedAddonFiltering;
import ruiseki.integratedtunnels.part.aspect.TunnelAspects;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.energy.capability.CapabilityEnergy;

/**
 * Interface for filtering energy storages.
 * 
 * @author rubensworks
 */
public class PartTypeInterfaceFilteringEnergy extends
    PartTypeInterfacePositionedAddonFiltering<IEnergyNetwork, IEnergyStorage, PartTypeInterfaceFilteringEnergy, PartTypeInterfaceFilteringEnergy.State> {

    public PartTypeInterfaceFilteringEnergy(String name) {
        super(name);
        AspectRegistry.getInstance()
            .register(this, Lists.<IAspect>newArrayList(TunnelAspects.Write.EnergyFilter.BOOLEAN_SET_FILTER));
    }

    @Override
    public Capability<IEnergyNetwork> getNetworkCapability() {
        return EnergyNetworkConfig.CAPABILITY;
    }

    @Override
    public Capability<IEnergyStorage> getTargetCapability() {
        return CapabilityEnergy.ENERGY;
    }

    @Override
    public LazyOptional<IEnergyStorage> getTargetCapabilityInstance(PartPos pos) {
        return EnergyHelpers.getEnergyStorage(pos);
    }

    @Override
    protected PartTypeInterfaceFilteringEnergy.State constructDefaultState() {
        return new PartTypeInterfaceFilteringEnergy.State(
            Aspects.REGISTRY.getWriteAspects(this)
                .size());
    }

    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.interfaceEnergyBaseConsumption;
    }

    public static class State extends
        PartTypeInterfacePositionedAddonFiltering.State<IEnergyNetwork, IEnergyStorage, PartTypeInterfaceFilteringEnergy, PartTypeInterfaceFilteringEnergy.State> {

        public State(int inventorySize) {
            super(inventorySize);
        }

        @Override
        public Capability<IEnergyStorage> getTargetCapability() {
            return CapabilityEnergy.ENERGY;
        }

        @Override
        public IEnergyStorage getCapabilityInstance() {
            return new PartTypeInterfaceEnergy.EnergyStorage(this);
        }
    }
}
