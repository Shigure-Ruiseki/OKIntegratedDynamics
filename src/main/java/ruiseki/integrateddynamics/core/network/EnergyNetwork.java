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
import ruiseki.integrateddynamics.core.helper.EnergyHelpers;

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
        return getChannel(element.getChannel()).extractEnergy(consumptionRate, true) == consumptionRate;
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
                getChannel(element.getChannel()).extractEnergy(consumptionRate, false);
            }
            ((IEnergyConsumingNetworkElement) element).postUpdate(getNetwork(), true);
        }
    }

    protected static int addSafe(int a, int b) {
        int add = a + b;
        if (add < a || add < b) return Integer.MAX_VALUE;
        return add;
    }

    @Override
    public boolean addPosition(PartPos pos, int priority, int channel) {
        IEnergyStorage energyStorage = EnergyHelpers.getEnergyStorage(pos);
        return energyStorage != null && super.addPosition(pos, priority, channel);
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

    protected IEnergyStorage getEnergyStorage(PrioritizedPartPos pos) {
        return isPositionDisabled(pos.getPartPos()) ? null : EnergyHelpers.getEnergyStorage(pos.getPartPos());
    }

    @Override
    public IEnergyStorage getChannel(int channel) {
        return new EnergyChannel(this, channel);
    }
}
