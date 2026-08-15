package ruiseki.integrateddynamics.capability.cable;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integrateddynamics.api.block.cable.ICable;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the cable capability.
 * 
 * @author rubensworks
 *
 */
public class CableConfig extends CapabilityConfig<ICable> {

    /**
     * The unique instance.
     */
    public static CableConfig _instance;

    @CapabilityInject(ICable.class)
    public static Capability<ICable> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public CableConfig() {
        super(CommonCapabilities._instance, true, "cable", "Cables form networks", ICable.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
