package ruiseki.integrateddynamics.core.network;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.Setter;
import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.api.block.IEnergyBattery;
import ruiseki.integrateddynamics.api.network.FullNetworkListenerAdapter;
import ruiseki.integrateddynamics.api.network.IEnergyConsumingNetworkElement;
import ruiseki.integrateddynamics.api.network.IEnergyNetwork;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.capability.energybattery.EnergyBatteryConfig;
import ruiseki.okcore.helper.CapabilityHelpers;

/**
 * A network that can hold energy.
 *
 * @author rubensworks
 */
public class EnergyNetwork extends FullNetworkListenerAdapter implements IEnergyNetwork {

    @Getter
    @Setter
    private INetwork network;
    private Set<PartPos> energyBatteryPositions = Sets.newHashSet();

    @Override
    public boolean canUpdate(INetworkElement element) {
        if (!(element instanceof IEnergyConsumingNetworkElement)) return true;
        int multiplier = GeneralConfig.energyConsumptionMultiplier;
        if (multiplier == 0) return true;
        int consumptionRate = ((IEnergyConsumingNetworkElement) element).getConsumptionRate() * multiplier;
        return consume(consumptionRate, true) == consumptionRate;
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
                consume(consumptionRate, false);
            }
            ((IEnergyConsumingNetworkElement) element).postUpdate(getNetwork(), true);
        }
    }

    protected synchronized List<IEnergyBattery> getMaterializedEnergyBatteries() {
        return ImmutableList
            .copyOf(Iterables.transform(energyBatteryPositions, new Function<PartPos, IEnergyBattery>() {

                @Nullable
                @Override
                public IEnergyBattery apply(PartPos pos) {
                    return CapabilityHelpers.getCapability(pos.getPos(), EnergyBatteryConfig.CAPABILITY)
                        .getOrNull();
                }

                @Override
                public boolean equals(@Nullable Object object) {
                    return false;
                }
            }));
    }

    protected int addSafe(int a, int b) {
        int add = a + b;
        if (add < a || add < b) return Integer.MAX_VALUE;
        return add;
    }

    @Override
    public synchronized int getStoredEnergy() {
        int energy = 0;
        for (IEnergyBattery energyBattery : getMaterializedEnergyBatteries()) {
            energy = addSafe(energy, energyBattery.getStoredEnergy());
        }
        return energy;
    }

    @Override
    public synchronized int getMaxStoredEnergy() {
        int maxEnergy = 0;
        for (IEnergyBattery energyBattery : getMaterializedEnergyBatteries()) {
            maxEnergy = addSafe(maxEnergy, energyBattery.getMaxStoredEnergy());
        }
        return maxEnergy;
    }

    @Override
    public int addEnergy(int energy, boolean simulate) {
        int toAdd = energy;
        for (IEnergyBattery energyBattery : getMaterializedEnergyBatteries()) {
            int maxAdd = Math.min(energyBattery.getMaxStoredEnergy() - energyBattery.getStoredEnergy(), toAdd);
            if (maxAdd > 0) {
                energyBattery.addEnergy(maxAdd, simulate);
            }
            toAdd -= maxAdd;
        }
        return energy - toAdd;
    }

    @Override
    public synchronized int consume(int energy, boolean simulate) {
        int toConsume = energy;
        for (IEnergyBattery energyBattery : getMaterializedEnergyBatteries()) {
            int consume = Math.min(energyBattery.getStoredEnergy(), toConsume);
            if (consume > 0) {
                toConsume -= energyBattery.consume(consume, simulate);
            }
        }
        return energy - toConsume;
    }

    @Override
    public boolean addEnergyBattery(PartPos pos) {
        IEnergyBattery energyBattery = CapabilityHelpers.getCapability(pos.getPos(), EnergyBatteryConfig.CAPABILITY)
            .getOrNull();
        if (energyBattery != null) {
            boolean contained = energyBatteryPositions.contains(pos);
            energyBatteryPositions.add(pos);
            return !contained;
        }
        return false;
    }

    @Override
    public void removeEnergyBattery(PartPos pos) {
        energyBatteryPositions.remove(pos);
    }

    @Override
    public Set<PartPos> getEnergyBatteries() {
        return Collections.unmodifiableSet(energyBatteryPositions);
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
}
