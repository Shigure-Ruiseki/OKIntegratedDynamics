package ruiseki.integrateddynamics.core.network;

import ruiseki.integrateddynamics.api.network.IEnergyConsumingNetworkElement;
import ruiseki.integrateddynamics.api.network.INetwork;

/**
 * Base implementation for an energy consuming network element.
 * 
 * @author rubensworks
 */
public abstract class ConsumingNetworkElementBase<N extends INetwork> extends NetworkElementBase<N>
    implements IEnergyConsumingNetworkElement<N> {

    @Override
    public int getConsumptionRate() {
        return 0;
    }

    @Override
    public void postUpdate(N network, boolean updated) {

    }
}
