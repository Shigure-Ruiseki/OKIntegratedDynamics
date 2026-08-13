package ruiseki.integratedtunnels.capability.network;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integratedtunnels.api.network.IItemNetwork;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the item network capability.
 * 
 * @author rubensworks
 *
 */
public class ItemNetworkConfig extends CapabilityConfig<IItemNetwork> {

    /**
     * The unique instance.
     */
    public static ItemNetworkConfig _instance;

    @CapabilityInject(IItemNetwork.class)
    public static Capability<IItemNetwork> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public ItemNetworkConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "itemNetwork",
            "A capability for networks that can hold items.",
            IItemNetwork.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
