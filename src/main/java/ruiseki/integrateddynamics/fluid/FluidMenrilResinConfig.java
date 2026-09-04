package ruiseki.integrateddynamics.fluid;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.FluidConfig;

/**
 * Config for {@link FluidMenrilResin}.
 *
 * @author rubensworks
 *
 */
public class FluidMenrilResinConfig extends FluidConfig {

    /**
     * The unique instance.
     */
    public static FluidMenrilResinConfig _instance;

    /**
     * Make a new instance.
     */
    public FluidMenrilResinConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "menril_resin",
            null,
            config -> new FluidMenrilResin(config.getNamedId()).setUnlocalizedName(config.getUnlocalizedName()));
    }
}
