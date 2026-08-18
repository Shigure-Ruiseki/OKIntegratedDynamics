package ruiseki.integrateddynamics.block;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.BlockConfig;

/**
 * Config for {@link BlockFluidMenrilResin}.
 * 
 * @author rubensworks
 *
 */
public class BlockFluidMenrilResinConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockFluidMenrilResinConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockFluidMenrilResinConfig() {
        super(IntegratedDynamics._instance, true, "block_menril_resin", null, BlockFluidMenrilResin.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
