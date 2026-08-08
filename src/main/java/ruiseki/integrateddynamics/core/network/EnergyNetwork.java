package ruiseki.integrateddynamics.core.network;

import cofh.api.energy.IEnergyStorage;
import lombok.Getter;
import lombok.Setter;
import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.api.network.IEnergyConsumingNetworkElement;
import ruiseki.integrateddynamics.api.network.IEnergyNetwork;
import ruiseki.integrateddynamics.api.network.IFullNetworkListener;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.path.IPathElement;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.helper.CapabilityHelpers;

/**
 * A network that can hold energy.
 *
 * @author rubensworks
 */
public class EnergyNetwork extends PositionedAddonsNetwork implements IEnergyNetwork, IFullNetworkListener {

    @Getter
    @Setter
    private INetwork network;

    @Override
    public boolean addNetworkElement(INetworkElement element, boolean networkPreinit) {
        return true;
    }

    @Override
    public boolean removeNetworkElementPre(INetworkElement element) {
        return true;
    }

    @Override
    public void removeNetworkElementPost(INetworkElement element) {

    }

    @Override
    public void kill() {

    }

    @Override
    public void update() {

    }

    @Override
    public boolean removePathElement(IPathElement pathElement) {
        return true;
    }

    @Override
    public void afterServerLoad() {

    }

    @Override
    public void beforeServerStop() {

    }

    @Override
    public boolean canUpdate(INetworkElement element) {
        if (!(element instanceof IEnergyConsumingNetworkElement)) return true;
        int multiplier = GeneralConfig.energyConsumptionMultiplier;
        if (multiplier == 0) return true;
        int consumptionRate = ((IEnergyConsumingNetworkElement) element).getConsumptionRate() * multiplier;
        return extractEnergy(consumptionRate, true) == consumptionRate;
    }

    @Override
    public void onSkipUpdate(INetworkElement element) {
        if (element instanceof IEnergyConsumingNetworkElement) {
            ((IEnergyConsumingNetworkElement) element).postUpdate(getNetwork(), false);
        }
    }

    @Override
    public void postUpdate(INetworkElement element) {
        if (element instanceof IEnergyConsumingNetworkElement) {
            int multiplier = GeneralConfig.energyConsumptionMultiplier;
            if (multiplier > 0) {
                int consumptionRate = ((IEnergyConsumingNetworkElement) element).getConsumptionRate() * multiplier;
                extractEnergy(consumptionRate, false);
            }
            ((IEnergyConsumingNetworkElement) element).postUpdate(getNetwork(), true);
        }
    }

    protected int addSafe(int a, int b) {
        int add = a + b;
        if (add < a || add < b) return Integer.MAX_VALUE;
        return add;
    }

    @Override
    public int getEnergyStored() {
        int energy = 0;
        for (PrioritizedPartPos partPos : getPositions()) {
            IEnergyStorage energyStorage = getEnergyStorage(partPos);
            if (energyStorage != null) {
                energy = addSafe(energy, energyStorage.getEnergyStored());
            }
        }
        return energy;
    }

    @Override
    public int getMaxEnergyStored() {
        int maxEnergy = 0;
        for (PrioritizedPartPos partPos : getPositions()) {
            IEnergyStorage energyStorage = getEnergyStorage(partPos);
            if (energyStorage != null) {
                maxEnergy = addSafe(maxEnergy, energyStorage.getMaxEnergyStored());
            }
        }
        return maxEnergy;
    }

    @Override
    public int receiveEnergy(int energy, boolean simulate) {
        int toAdd = energy;
        for (PrioritizedPartPos partPos : getPositions()) {
            IEnergyStorage energyStorage = getEnergyStorage(partPos);
            if (energyStorage != null) {
                toAdd -= energyStorage.receiveEnergy(toAdd, simulate);
            }
        }
        return energy - toAdd;
    }

    @Override
    public int extractEnergy(int energy, boolean simulate) {
        int toConsume = energy;
        for (PrioritizedPartPos partPos : getPositions()) {
            IEnergyStorage energyStorage = getEnergyStorage(partPos);
            if (energyStorage != null) {
                toConsume -= energyStorage.extractEnergy(toConsume, simulate);
            }
        }
        return energy - toConsume;
    }

    @Override
    public boolean addPosition(PartPos pos, int priority) {
        IEnergyStorage energyStorage = CapabilityHelpers
            .getCapability(pos.getPos(), CapabilityEnergy.ENERGY, pos.getSide())
            .getOrNull();
        return energyStorage != null && super.addPosition(pos, priority);
    }

    @Override
    public int getConsumptionRate() {
        int multiplier = GeneralConfig.energyConsumptionMultiplier;
        if (multiplier == 0) return 0;
        int consumption = 0;
        for (INetworkElement element : getNetwork().getElements()) {
            consumption += ((IEnergyConsumingNetworkElement) element).getConsumptionRate() * multiplier;
        }
        return consumption;
    }

    protected static IEnergyStorage getEnergyStorage(PrioritizedPartPos pos) {
        return CapabilityHelpers.getCapability(
            pos.getPartPos()
                .getPos(),
            CapabilityEnergy.ENERGY,
            pos.getPartPos()
                .getSide())
            .getOrNull();
    }
}
