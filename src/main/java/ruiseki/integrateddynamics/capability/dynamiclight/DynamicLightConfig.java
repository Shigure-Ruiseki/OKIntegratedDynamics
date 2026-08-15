package ruiseki.integrateddynamics.capability.dynamiclight;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integrateddynamics.api.block.IDynamicLight;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the dynamic light capability.
 *
 * @author rubensworks
 *
 */
public class DynamicLightConfig extends CapabilityConfig<IDynamicLight> {

    /**
     * The unique instance.
     */
    public static DynamicLightConfig _instance;

    @CapabilityInject(IDynamicLight.class)
    public static Capability<IDynamicLight> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public DynamicLightConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "dynamic_light",
            "Allows light level modifications.",
            IDynamicLight.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
