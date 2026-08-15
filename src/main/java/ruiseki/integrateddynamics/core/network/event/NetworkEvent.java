package ruiseki.integrateddynamics.core.network.event;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.event.INetworkEvent;

/**
 * An event posted in the {@link ruiseki.integrateddynamics.api.network.IPartNetwork} event bus.
 *
 * @author rubensworks
 */
public class NetworkEvent implements INetworkEvent {

    private final INetwork network;

    public NetworkEvent(INetwork network) {
        this.network = network;
    }

    @Override
    public INetwork getNetwork() {
        return this.network;
    }

}
