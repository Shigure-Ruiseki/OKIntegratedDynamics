package ruiseki.integrateddynamics.capability.path;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integrateddynamics.api.path.IPathElement;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the path element capability.
 *
 * @author rubensworks
 *
 */
public class PathElementConfig extends CapabilityConfig<IPathElement> {

    /**
     * The unique instance.
     */
    public static PathElementConfig _instance;

    @CapabilityInject(IPathElement.class)
    public static Capability<IPathElement> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public PathElementConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "path_element_provider",
            "Capability for elements used for path construction",
            IPathElement.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
