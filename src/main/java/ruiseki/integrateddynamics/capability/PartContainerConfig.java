package ruiseki.integrateddynamics.capability;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.CapabilityHelpers;

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
            "partContainer",
            "A container that can hold parts.",
            IPartContainer.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

    /**
     * Get the part container at the given position.
     * 
     * @param pos The position.
     * @return The container or null.
     */
    public static IPartContainer get(DimPos pos) {
        return CapabilityHelpers.getCapability(pos, PartContainerConfig.CAPABILITY, ForgeDirection.UNKNOWN)
            .getOrNull();
    }
}
