package ruiseki.integrateddynamics.capability.network;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkCarrier;

/**
 * Default implementation of {@link INetworkCarrier}.
 *
 * @author rubensworks
 */
public class NetworkCarrierDefault implements INetworkCarrier {

    private INetwork network;

    @Override
    public void setNetwork(@Nullable INetwork network) {
        this.network = network;
    }

    @Nullable
    @Override
    public INetwork getNetwork() {
        return network;
    }
}
