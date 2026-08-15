package ruiseki.integratedcrafting.capability.network;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integratedcrafting.api.network.ICraftingNetwork;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the crafting network capability.
 * 
 * @author rubensworks
 *
 */
public class CraftingNetworkConfig extends CapabilityConfig<ICraftingNetwork> {

    /**
     * The unique instance.
     */
    public static CraftingNetworkConfig _instance;

    @CapabilityInject(ICraftingNetwork.class)
    public static Capability<ICraftingNetwork> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public CraftingNetworkConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "craftingNetwork",
            "A capability for crafting networks.",
            ICraftingNetwork.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
