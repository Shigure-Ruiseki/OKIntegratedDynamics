package ruiseki.integrateddynamics.capability.network;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkCarrier;

/**
 * Default implementation of {@link INetworkCarrier}.
 * 
 * @author rubensworks
 */
public class NetworkCarrierDefault<N extends INetwork> implements INetworkCarrier<N> {

    private N network;

    @Override
    public void setNetwork(@Nullable N network) {
        this.network = network;
    }

    @Nullable
    @Override
    public N getNetwork() {
        return network;
    }
}
