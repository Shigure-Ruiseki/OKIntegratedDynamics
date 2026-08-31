package ruiseki.integrateddynamics.core.network;

import lombok.Getter;
import lombok.Setter;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.api.network.IEnergyConsumingNetworkElement;
import ruiseki.integrateddynamics.api.network.IEnergyNetwork;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;

/**
 * A network that can hold energy.
 *
 * @author rubensworks
 */
public class EnergyNetwork extends PositionedAddonsNetworkIngredients<Long, Boolean> implements IEnergyNetwork {

    @Getter
    @Setter
    private INetwork network;

    public EnergyNetwork(IngredientComponent<Long, Boolean> component) {
        super(component);
    }

    @Override
    public boolean canUpdate(INetworkElement element) {
        if (!(element instanceof IEnergyConsumingNetworkElement)) return true;
        int multiplier = GeneralConfig.energyConsumptionMultiplier;
        if (multiplier == 0) return true;
        int consumptionRate = ((IEnergyConsumingNetworkElement) element).getConsumptionRate() * multiplier;
        return getChannel(element.getChannel()).extract(consumptionRate, true) == consumptionRate;
    }

    @Override
    public void onSkipUpdate(INetworkElement element) {
        if (element instanceof IEnergyConsumingNetworkElement consumingNetworkElement) {
            consumingNetworkElement.postUpdate(getNetwork(), false);
        }
    }

    @Override
    public void postUpdate(INetworkElement element) {
        if (element instanceof IEnergyConsumingNetworkElement consumingNetworkElement) {
            int multiplier = GeneralConfig.energyConsumptionMultiplier;
            if (multiplier > 0) {
                int consumptionRate = consumingNetworkElement.getConsumptionRate() * multiplier;
                getChannel(element.getChannel()).extract(consumptionRate, false);
            }
            consumingNetworkElement.postUpdate(getNetwork(), true);
        }
    }

    @Override
    public int getConsumptionRate() {
        int multiplier = GeneralConfig.energyConsumptionMultiplier;
        if (multiplier == 0) return 0;
        int consumption = 0;
        for (INetworkElement element : getNetwork().getElements()) {
            if (element instanceof IEnergyConsumingNetworkElement consuming) {
                consumption += consuming.getConsumptionRate() * multiplier;
            }
        }
        return consumption;
    }

    @Override
    public long getRateLimit() {
        return GeneralConfig.energyRateLimit;
    }
}
