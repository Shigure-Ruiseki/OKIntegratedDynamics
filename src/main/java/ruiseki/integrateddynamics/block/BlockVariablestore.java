package ruiseki.integrateddynamics.block;

import ruiseki.integrateddynamics.core.block.BlockContainerGuiCabled;
import ruiseki.integrateddynamics.tileentity.TileVariablestore;
import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.DirectionProperty;

/**
 * A block that can hold defined variables so that they can be referred to elsewhere in the network.
 *
 * @author rubensworks
 */
public class BlockVariablestore extends BlockContainerGuiCabled {

    @BlockProperty
    public static final DirectionProperty FACING = DirectionProperty.facing();

    /**
     * Make a new block instance.
     */
    public BlockVariablestore() {
        super(TileVariablestore.class);
    }

    @Override
    public boolean saveNBTToDroppedItem() {
        return false;
    }
}
