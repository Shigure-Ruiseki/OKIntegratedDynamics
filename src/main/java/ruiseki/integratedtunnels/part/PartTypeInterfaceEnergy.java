package ruiseki.integratedtunnels.part;

import cofh.api.energy.IEnergyStorage;
import ruiseki.integrateddynamics.api.network.IEnergyNetwork;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.capability.network.EnergyNetworkConfig;
import ruiseki.integrateddynamics.core.helper.EnergyHelpers;
import ruiseki.integratedtunnels.GeneralConfig;
import ruiseki.integratedtunnels.core.part.PartTypeInterfacePositionedAddon;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.energy.capability.CapabilityEnergy;

/**
 * Interface for energy storages.
 * 
 * @author rubensworks
 */
public class PartTypeInterfaceEnergy extends
    PartTypeInterfacePositionedAddon<IEnergyNetwork, IEnergyStorage, PartTypeInterfaceEnergy, PartTypeInterfaceEnergy.State> {

    public PartTypeInterfaceEnergy(String name) {
        super(name);
    }

    @Override
    protected Capability<IEnergyNetwork> getNetworkCapability() {
        return EnergyNetworkConfig.CAPABILITY;
    }

    @Override
    protected Capability<IEnergyStorage> getTargetCapability() {
        return CapabilityEnergy.ENERGY;
    }

    @Override
    protected IEnergyStorage getTargetCapabilityInstance(PartPos pos) {
        return EnergyHelpers.getEnergyStorage(pos);
    }

    @Override
    protected PartTypeInterfaceEnergy.State constructDefaultState() {
        return new PartTypeInterfaceEnergy.State();
    }

    @Override
    public int getConsumptionRate(State state) {
        return GeneralConfig.interfaceEnergyBaseConsumption;
    }

    public static class State
        extends PartTypeInterfacePositionedAddon.State<PartTypeInterfaceEnergy, IEnergyNetwork, IEnergyStorage>
        implements IEnergyStorage {

        @Override
        protected Capability<IEnergyStorage> getTargetCapability() {
            return CapabilityEnergy.ENERGY;
        }

        protected IEnergyStorage getEnergyStorage() {
            return getPositionedAddonsNetwork().getChannelExternal(CapabilityEnergy.ENERGY, getChannel());
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!isNetworkAndPositionValid()) {
                return 0;
            }
            disablePosition();
            int ret = getEnergyStorage().receiveEnergy(maxReceive, simulate);
            enablePosition();
            return ret;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (!isNetworkAndPositionValid()) {
                return 0;
            }
            disablePosition();
            int ret = getEnergyStorage().extractEnergy(maxExtract, simulate);
            enablePosition();
            return ret;
        }

        @Override
        public int getEnergyStored() {
            if (!isNetworkAndPositionValid()) {
                return 0;
            }
            disablePosition();
            int ret = getEnergyStorage().getEnergyStored();
            enablePosition();
            return ret;
        }

        @Override
        public int getMaxEnergyStored() {
            if (!isNetworkAndPositionValid()) {
                return 0;
            }
            disablePosition();
            int ret = getEnergyStorage().getMaxEnergyStored();
            enablePosition();
            return ret;
        }
    }
}
