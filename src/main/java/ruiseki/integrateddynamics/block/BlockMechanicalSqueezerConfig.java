package ruiseki.integrateddynamics.block;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.ConfigurableProperty;
import ruiseki.okcore.config.ConfigurableTypeCategory;
import ruiseki.okcore.config.extendedconfig.BlockContainerConfig;

/**
 * Config for {@link BlockMechanicalSqueezer}.
 * 
 * @author rubensworks
 */
public class BlockMechanicalSqueezerConfig extends BlockContainerConfig {

    /**
     * The unique instance.
     */
    public static BlockMechanicalSqueezerConfig _instance;

    /**
     * The energy capacity of a mechanical squeezer.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.MACHINE,
        comment = "The energy capacity of a mechanical squeezer.",
        minimalValue = 0)
    public static int capacity = 100000;

    /**
     * The energy consumption rate.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.MACHINE,
        comment = "The energy consumption rate.",
        minimalValue = 0)
    public static int consumptionRate = 80;

    /**
     * How many mB per tick can be auto-ejected.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.MACHINE,
        comment = "How many mB per tick can be auto-ejected.",
        minimalValue = 0)
    public static int autoEjectFluidRate = 500;

    /**
     * Make a new instance.
     */
    public BlockMechanicalSqueezerConfig() {
        super(IntegratedDynamics._instance, true, "mechanical_squeezer", null, BlockMechanicalSqueezer::new);
    }
}
