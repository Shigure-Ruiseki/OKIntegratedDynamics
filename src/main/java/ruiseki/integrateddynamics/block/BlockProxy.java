package ruiseki.integrateddynamics.block;

import ruiseki.integrateddynamics.core.block.BlockContainerGuiCabled;
import ruiseki.integrateddynamics.tileentity.TileProxy;
import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.DirectionProperty;

/**
 * A block that can expose variables.
 *
 * @author rubensworks
 */
public class BlockProxy extends BlockContainerGuiCabled {

    @BlockProperty
    public static final DirectionProperty FACING = DirectionProperty.facing();

    /**
     * Make a new block instance.
     */
    public BlockProxy() {
        super(TileProxy.class);
    }
}
