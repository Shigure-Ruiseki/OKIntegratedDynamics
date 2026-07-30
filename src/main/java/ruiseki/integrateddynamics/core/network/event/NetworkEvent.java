package ruiseki.integrateddynamics.core.network.event;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.event.INetworkEvent;

/**
 * An event posted in the {@link ruiseki.integrateddynamics.api.network.IPartNetwork} event bus.
 * 
 * @author rubensworks
 */
public class NetworkEvent<N extends INetwork<N>> implements INetworkEvent<N> {

    private final N network;

    public NetworkEvent(N network) {
        this.network = network;
    }

    @Override
    public N getNetwork() {
        return this.network;
    }

}
