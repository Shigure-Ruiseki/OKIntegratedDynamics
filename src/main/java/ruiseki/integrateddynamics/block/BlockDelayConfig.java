package ruiseki.integrateddynamics.block;

import net.minecraft.item.ItemBlock;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.item.ItemBlockDelay;
import ruiseki.okcore.config.ConfigurableProperty;
import ruiseki.okcore.config.ConfigurableTypeCategory;
import ruiseki.okcore.config.extendedconfig.BlockContainerConfig;

/**
 * Config for {@link BlockDelay}.
 *
 * @author rubensworks
 */
public class BlockDelayConfig extends BlockContainerConfig {

    /**
     * The unique instance.
     */
    public static BlockDelayConfig _instance;

    /**
     * The maximum value history length that can be maintained.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.MACHINE,
        comment = "The maximum value history length that can be maintained..",
        minimalValue = 1)
    public static int maxHistoryCapacity = 1024;

    /**
     * Make a new instance.
     */
    public BlockDelayConfig() {
        super(IntegratedDynamics._instance, true, "delay", null, BlockDelay::new);
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockDelay.class;
    }
}
