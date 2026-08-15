package ruiseki.integrateddynamics.block;

import ruiseki.okcore.config.configurable.ConfigurableBlockLog;
import ruiseki.okcore.config.extendedconfig.BlockConfig;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;

/**
 * Menril log block.
 *
 * @author rubensworks
 */
public class BlockMenrilLog extends ConfigurableBlockLog {

    private static BlockMenrilLog _instance = null;

    /**
     * Get the unique instance.
     *
     * @return The instance.
     */
    public static BlockMenrilLog getInstance() {
        return _instance;
    }

    /**
     * Make a new block instance.
     *
     * @param eConfig Config for this block.
     */
    public BlockMenrilLog(ExtendedConfig<BlockConfig> eConfig) {
        super(eConfig);
        this.setHardness(2.0F);
        this.setStepSound(soundTypeWood);
    }
}
