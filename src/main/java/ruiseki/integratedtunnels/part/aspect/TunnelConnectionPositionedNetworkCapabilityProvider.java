package ruiseki.integratedtunnels.part.aspect;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.okcore.capabilities.ICapabilityProvider;

/**
 * A tunnel connection over a network at a certain position.
 * 
 * @author rubensworks
 */
public class TunnelConnectionPositionedNetworkCapabilityProvider extends TunnelConnectionPositionedNetwork {

    @Nullable
    private final ICapabilityProvider capabilityProvider;

    public TunnelConnectionPositionedNetworkCapabilityProvider(INetwork network, int channel, PartPos pos,
        ITunnelTransfer transfer, @Nullable ICapabilityProvider capabilityProvider) {
        super(network, channel, pos, transfer);
        this.capabilityProvider = capabilityProvider;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TunnelConnectionPositionedNetworkCapabilityProvider)) {
            return false;
        }
        TunnelConnectionPositionedNetworkCapabilityProvider that = (TunnelConnectionPositionedNetworkCapabilityProvider) obj;
        return Objects.equals(this.capabilityProvider, that.capabilityProvider) && super.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.capabilityProvider == null ? 0 : this.capabilityProvider.hashCode() ^ super.hashCode();
    }
}
