package ruiseki.integrateddynamics.capability.facadeable;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integrateddynamics.api.block.IFacadeable;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the facadeable capability.
 *
 * @author rubensworks
 *
 */
public class FacadeableConfig extends CapabilityConfig<IFacadeable> {

    /**
     * The unique instance.
     */
    public static FacadeableConfig _instance;

    @CapabilityInject(IFacadeable.class)
    public static Capability<IFacadeable> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public FacadeableConfig() {
        super(CommonCapabilities._instance, true, "facadeable", "Can hold a facade", IFacadeable.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }
}
