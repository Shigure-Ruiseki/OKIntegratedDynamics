package ruiseki.integrateddynamics.capability.path;

import net.minecraft.tileentity.TileEntity;

import ruiseki.integrateddynamics.api.block.cable.ICable;
import ruiseki.integrateddynamics.api.path.IPathElement;
import ruiseki.okcore.datastructure.DimPos;

/**
 * Implementation of {@link IPathElement} for a tile entity.
 *
 * @author rubensworks
 */
public class PathElementTile<T extends TileEntity> extends PathElementCable {

    private final T tile;
    private final ICable cable;
    private final DimPos position;

    public PathElementTile(T tile, ICable cable) {
        this.tile = tile;
        this.cable = cable;
        this.position = DimPos.of(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
    }

    protected T getTile() {
        return tile;
    }

    @Override
    protected ICable getCable() {
        return cable;
    }

    @Override
    public DimPos getPosition() {
        return position;
    }
}
