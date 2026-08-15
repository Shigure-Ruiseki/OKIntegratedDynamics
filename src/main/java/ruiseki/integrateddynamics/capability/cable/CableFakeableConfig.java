package ruiseki.integrateddynamics.capability.cable;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integrateddynamics.api.block.cable.ICableFakeable;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the fakeable cable capability.
 * 
 * @author rubensworks
 *
 */
public class CableFakeableConfig extends CapabilityConfig<ICableFakeable> {

    /**
     * The unique instance.
     */
    public static CableFakeableConfig _instance;

    @CapabilityInject(ICableFakeable.class)
    public static Capability<ICableFakeable> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public CableFakeableConfig() {
        super(CommonCapabilities._instance, true, "cableFakeable", "Cables that can become fake", ICableFakeable.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
