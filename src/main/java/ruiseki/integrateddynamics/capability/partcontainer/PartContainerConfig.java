package ruiseki.integrateddynamics.capability.partcontainer;

import net.minecraft.world.World;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;
import ruiseki.okcore.datastructure.BlockPos;
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
        return get(pos.getWorld(), pos.getBlockPos());
    }

    /**
     * Get the part container at the given position.
     *
     * @param world The world.
     * @param pos   The block position.
     * @return The container or null.
     */
    public static IPartContainer get(World world, BlockPos pos) {
        return CapabilityHelpers.getCapability(world, pos, PartContainerConfig.CAPABILITY, null)
            .getOrNull();
    }
}
