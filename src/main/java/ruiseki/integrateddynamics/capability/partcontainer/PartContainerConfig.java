package ruiseki.integrateddynamics.capability.partcontainer;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the part container capability.
 *
 * @author rubensworks
 *
 */
public class PartContainerConfig extends CapabilityConfig<IPartContainer> {

    /**
     * The unique instance.
     */
    public static PartContainerConfig _instance;

    @CapabilityInject(IPartContainer.class)
    public static Capability<IPartContainer> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public PartContainerConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "part_container",
            "A container that can hold parts.",
            IPartContainer.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }
}
