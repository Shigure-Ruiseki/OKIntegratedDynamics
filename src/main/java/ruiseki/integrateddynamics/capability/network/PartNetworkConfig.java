package ruiseki.integrateddynamics.capability.network;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the part network capability.
 * 
 * @author rubensworks
 *
 */
public class PartNetworkConfig extends CapabilityConfig<IPartNetwork> {

    /**
     * The unique instance.
     */
    public static PartNetworkConfig _instance;

    @CapabilityInject(IPartNetwork.class)
    public static Capability<IPartNetwork> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public PartNetworkConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "partNetwork",
            "A capability for adding parts to a network.",
            IPartNetwork.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
