package ruiseki.integrateddynamics.block;

import ruiseki.okcore.block.BlockLogBase;

/**
 * Menril log block.
 *
 * @author rubensworks
 */
public class BlockMenrilLog extends BlockLogBase {

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
     */
    public BlockMenrilLog() {
        super();
        this.setHardness(2.0F);
        this.setStepSound(soundTypeWood);
    }
}
