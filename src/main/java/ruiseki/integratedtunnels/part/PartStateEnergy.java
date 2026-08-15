package ruiseki.integratedtunnels.part;

import cofh.api.energy.IEnergyStorage;
import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.api.network.IEnergyNetwork;
import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;
import ruiseki.integratedtunnels.core.part.PartStatePositionedAddon;
import ruiseki.okcore.energy.capability.CapabilityEnergy;

/**
 * A part state for handling energy import and export.
 * It also acts as an energy capability that can be added to itself.
 * 
 * @author rubensworks
 */
public class PartStateEnergy<P extends IPartTypeWriter> extends PartStatePositionedAddon<P, IEnergyNetwork>
    implements IEnergyStorage {

    public PartStateEnergy(int inventorySize, boolean canReceive, boolean canExtract) {
        super(inventorySize, canReceive, canExtract);
    }

    protected IEnergyStorage getEnergyStorage() {
        return getPositionedAddonsNetwork().getChannelExternal(CapabilityEnergy.ENERGY, getChannel());
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        maxReceive = Math.min(maxReceive, GeneralConfig.energyRateLimit);
        return this.canReceive() && getPositionedAddonsNetwork() != null
            ? getEnergyStorage().receiveEnergy(maxReceive, simulate)
            : 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        maxExtract = Math.min(maxExtract, GeneralConfig.energyRateLimit);
        return this.canExtract() && getPositionedAddonsNetwork() != null
            ? getEnergyStorage().extractEnergy(maxExtract, simulate)
            : 0;
    }

    @Override
    public int getEnergyStored() {
        return getPositionedAddonsNetwork() != null ? getEnergyStorage().getEnergyStored() : 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return getPositionedAddonsNetwork() != null ? getEnergyStorage().getMaxEnergyStored() : 0;
    }
}
