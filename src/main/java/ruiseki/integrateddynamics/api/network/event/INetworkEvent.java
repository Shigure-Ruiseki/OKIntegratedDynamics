package ruiseki.integrateddynamics.api.network.event;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;

/**
 * An event posted in the {@link IPartNetwork} event bus.
 * 
 * @author rubensworks
 */
public interface INetworkEvent<N extends INetwork<N>> {

    /**
     * @return The network this event is thrown in.
     */
    public N getNetwork();

}
