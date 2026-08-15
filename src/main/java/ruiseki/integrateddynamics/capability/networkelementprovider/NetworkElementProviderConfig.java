package ruiseki.integrateddynamics.capability.networkelementprovider;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integrateddynamics.api.network.INetworkElementProvider;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the network element provider capability.
 *
 * @author rubensworks
 *
 */
public class NetworkElementProviderConfig extends CapabilityConfig<INetworkElementProvider> {

    /**
     * The unique instance.
     */
    public static NetworkElementProviderConfig _instance;

    @CapabilityInject(INetworkElementProvider.class)
    public static Capability<INetworkElementProvider> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public NetworkElementProviderConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "network_element_provider",
            "Providers network elements.",
            INetworkElementProvider.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }
}
