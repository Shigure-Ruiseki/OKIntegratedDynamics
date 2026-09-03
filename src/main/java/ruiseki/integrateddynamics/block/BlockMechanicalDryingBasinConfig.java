package ruiseki.integrateddynamics.block;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.ConfigurableProperty;
import ruiseki.okcore.config.extendedconfig.BlockContainerConfig;

/**
 * Config for {@link BlockMechanicalDryingBasin}.
 *
 * @author rubensworks
 */
public class BlockMechanicalDryingBasinConfig extends BlockContainerConfig {

    /**
     * The unique instance.
     */
    public static BlockMechanicalDryingBasinConfig _instance;

    /**
     * The energy capacity of a mechanical drying basin.
     */
    @ConfigurableProperty(
        category = "machine",
        comment = "The energy capacity of a mechanical drying basin.",
        minimalValue = 0)
    public static int capacity = 100000;

    /**
     * The energy consumption rate.
     */
    @ConfigurableProperty(category = "machine", comment = "The energy consumption rate.", minimalValue = 0)
    public static int consumptionRate = 80;

    /**
     * Make a new instance.
     */
    public BlockMechanicalDryingBasinConfig() {
        super(IntegratedDynamics._instance, true, "mechanical_drying_basin", null, BlockMechanicalDryingBasin::new);
    }
}
