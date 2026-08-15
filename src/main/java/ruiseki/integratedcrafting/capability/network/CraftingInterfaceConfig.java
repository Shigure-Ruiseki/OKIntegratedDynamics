package ruiseki.integratedcrafting.capability.network;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integratedcrafting.api.crafting.ICraftingInterface;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the crafting interface capability.
 * 
 * @author rubensworks
 *
 */
public class CraftingInterfaceConfig extends CapabilityConfig<ICraftingInterface> {

    /**
     * The unique instance.
     */
    public static CraftingInterfaceConfig _instance;

    @CapabilityInject(ICraftingInterface.class)
    public static Capability<ICraftingInterface> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public CraftingInterfaceConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "craftingInterface",
            "A capability for crafting interfaces.",
            ICraftingInterface.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
