package ruiseki.integrateddynamics.capability.facadeable;

import net.minecraft.world.IBlockAccess;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integrateddynamics.api.block.IFacadeable;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.CapabilityHelpers;

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

    public static boolean hasFacade(IBlockAccess world, BlockPos pos) {
        IFacadeable facadeable = CapabilityHelpers.getCapability(world, pos, CAPABILITY, null)
            .getOrNull();
        return facadeable != null && facadeable.hasFacade();
    }

    public static BlockState getFacade(IBlockAccess world, BlockPos pos) {
        IFacadeable facadeable = CapabilityHelpers.getCapability(world, pos, CAPABILITY, null)
            .getOrNull();
        return facadeable != null ? facadeable.getFacade() : null;
    }

}
