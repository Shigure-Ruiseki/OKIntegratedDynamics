package ruiseki.integrateddynamics.core.network;

import ruiseki.integrateddynamics.api.network.IEnergyConsumingNetworkElement;
import ruiseki.integrateddynamics.api.network.INetwork;

/**
 * Base implementation for an energy consuming network element.
 *
 * @author rubensworks
 */
public abstract class ConsumingNetworkElementBase extends NetworkElementBase implements IEnergyConsumingNetworkElement {

    @Override
    public boolean isUpdate() {
        return getConsumptionRate() > 0 || super.isUpdate();
    }

    @Override
    public int getConsumptionRate() {
        return 0;
    }

    @Override
    public void postUpdate(INetwork network, boolean updated) {

    }
}
