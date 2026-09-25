package ruiseki.integrateddynamics.block;

import ruiseki.integrateddynamics.core.block.BlockContainerGuiCabled;
import ruiseki.integrateddynamics.tileentity.TileDelay;
import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.DirectionProperty;

/**
 * A block that can delay variables.
 *
 * @author rubensworks
 */
public class BlockDelay extends BlockContainerGuiCabled {

    @BlockProperty
    public static final DirectionProperty FACING = DirectionProperty.facing();

    /**
     * Make a new block instance.
     */
    public BlockDelay() {
        super(TileDelay.class);
    }
}
