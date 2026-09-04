package ruiseki.integrateddynamics.block;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.BlockConfig;

/**
 * Config for {@link BlockFluidMenrilResin}.
 *
 * @author rubensworks
 *
 */
public class BlockFluidLiquidChorusConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockFluidLiquidChorusConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockFluidLiquidChorusConfig() {
        super(IntegratedDynamics._instance, true, "block_liquid_chorus", null, config -> new BlockFluidLiquidChorus());
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
