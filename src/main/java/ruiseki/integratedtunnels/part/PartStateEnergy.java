package ruiseki.integratedtunnels.part;

import cofh.api.energy.IEnergyStorage;
import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.api.network.IEnergyNetwork;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;
import ruiseki.integratedtunnels.core.TunnelHelpers;
import ruiseki.integratedtunnels.core.part.PartStatePositionedAddon;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.energy.capability.CapabilityEnergy;

/**
 * A part state for handling energy import and export.
 * It also acts as an energy capability that can be added to itself.
 *
 * @author rubensworks
 */
public class PartStateEnergy<P extends IPartTypeWriter> extends PartStatePositionedAddon<P, IEnergyNetwork, Long>
    implements IEnergyStorage {

    public PartStateEnergy(int inventorySize, boolean canReceive, boolean canExtract) {
        super(inventorySize, canReceive, canExtract);
    }

    @Override
    public <T2> LazyOptional<T2> getCapability(Capability<T2> capability, INetwork network, IPartNetwork partNetwork,
        PartTarget target) {
        if (capability == CapabilityEnergy.ENERGY) {
            return LazyOptional.of(() -> this)
                .cast();
        }
        return super.getCapability(capability, network, partNetwork, target);
    }

    protected IEnergyStorage getEnergyStorage() {
        return getPositionedAddonsNetwork()
            .getChannelExternal(CapabilityEnergy.ENERGY, TunnelHelpers.getPassiveInteractionChannel(this));
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        maxReceive = Math.min(maxReceive, GeneralConfig.energyRateLimit);
        return this.canReceive() && getPositionedAddonsNetwork() != null
            && getStorageFilter() != null
            && getStorageFilter().testInsertion((long) maxReceive)
                ? getEnergyStorage().receiveEnergy(maxReceive, simulate)
                : 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        maxExtract = Math.min(maxExtract, GeneralConfig.energyRateLimit);
        return this.canExtract() && getPositionedAddonsNetwork() != null
            && getStorageFilter() != null
            && getStorageFilter().testExtraction((long) maxExtract)
                ? getEnergyStorage().extractEnergy(maxExtract, simulate)
                : 0;
    }

    @Override
    public int getEnergyStored() {
        if (getPositionedAddonsNetwork() != null && getStorageFilter() != null) {
            int stored = getEnergyStorage().getEnergyStored();
            if (getStorageFilter().testView((long) stored)) {
                return stored;
            }
        }
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return getPositionedAddonsNetwork() != null && getStorageFilter() != null
            ? getEnergyStorage().getMaxEnergyStored()
            : 0;
    }
}
