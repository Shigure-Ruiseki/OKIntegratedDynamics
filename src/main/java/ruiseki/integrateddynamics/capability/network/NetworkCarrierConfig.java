package ruiseki.integrateddynamics.capability.network;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integrateddynamics.api.network.INetworkCarrier;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the network carrier capability.
 * 
 * @author rubensworks
 *
 */
public class NetworkCarrierConfig extends CapabilityConfig<INetworkCarrier> {

    /**
     * The unique instance.
     */
    public static NetworkCarrierConfig _instance;

    @CapabilityInject(INetworkCarrier.class)
    public static Capability<INetworkCarrier> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public NetworkCarrierConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "networkCarrier",
            "Capability that can hold networks",
            INetworkCarrier.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
