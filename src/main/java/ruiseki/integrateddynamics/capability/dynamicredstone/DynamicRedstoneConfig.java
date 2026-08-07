package ruiseki.integrateddynamics.capability.dynamicredstone;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integrateddynamics.api.block.IDynamicRedstone;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the dynamic redstone capability.
 *
 * @author rubensworks
 *
 */
public class DynamicRedstoneConfig extends CapabilityConfig<IDynamicRedstone> {

    /**
     * The unique instance.
     */
    public static DynamicRedstoneConfig _instance;

    @CapabilityInject(IDynamicRedstone.class)
    public static Capability<IDynamicRedstone> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public DynamicRedstoneConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "dynamicRedstone",
            "Allows redstone level modifications.",
            IDynamicRedstone.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
