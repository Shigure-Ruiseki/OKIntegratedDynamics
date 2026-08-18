package ruiseki.integrateddynamics.fluid;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.FluidConfig;

/**
 * Config for {@link FluidLiquidChorus}.
 * 
 * @author rubensworks
 *
 */
public class FluidLiquidChorusConfig extends FluidConfig {

    /**
     * The unique instance.
     */
    public static FluidLiquidChorusConfig _instance;

    /**
     * Make a new instance.
     */
    public FluidLiquidChorusConfig() {
        super(IntegratedDynamics._instance, true, "liquidchorus", null, FluidLiquidChorus.class);
    }
}
