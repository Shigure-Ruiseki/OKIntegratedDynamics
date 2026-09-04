package ruiseki.integrateddynamics.block;

import ruiseki.okcore.block.BlockLogBase;

/**
 * Menril log block.
 *
 * @author rubensworks
 */
public class BlockMenrilLog extends BlockLogBase {

    /**
     * Make a new block instance.
     */
    public BlockMenrilLog() {
        super();
        this.setHardness(2.0F);
        this.setStepSound(soundTypeWood);
    }
}
