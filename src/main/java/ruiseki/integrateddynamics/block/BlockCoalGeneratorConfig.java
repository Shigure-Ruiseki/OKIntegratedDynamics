package ruiseki.integrateddynamics.block;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.ConfigurableProperty;
import ruiseki.okcore.config.extendedconfig.BlockContainerConfig;

/**
 * Config for {@link BlockCoalGenerator}.
 *
 * @author rubensworks
 */
public class BlockCoalGeneratorConfig extends BlockContainerConfig {

    @ConfigurableProperty(
        category = "machine",
        comment = "The energy production rate (in RF/t) of the coal generator.",
        minimalValue = 1)
    public static int energyPerTick = 20;

    /**
     * The unique instance.
     */
    public static BlockCoalGeneratorConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockCoalGeneratorConfig() {
        super(IntegratedDynamics._instance, true, "coal_generator", null, BlockCoalGenerator::new);
    }
}
