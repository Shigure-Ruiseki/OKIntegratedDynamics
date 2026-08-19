package ruiseki.integrateddynamics.core.block;

import net.minecraft.block.material.Material;

import ruiseki.integrateddynamics.core.tileentity.TileMultipartTicking;
import ruiseki.okcore.block.BlockTile;

/**
 * A block that is buildReader up from different parts.
 * This block refers to a ticking part entity.
 *
 * @author rubensworks
 */
public abstract class BlockMultipartTicking extends BlockTile {

    /**
     * Make a new block instance.
     *
     * @param material The material for this block.
     */
    public BlockMultipartTicking(Material material) {
        super(material, TileMultipartTicking.class);
    }

}
