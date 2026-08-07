package ruiseki.integrateddynamics.capability.path;

import net.minecraft.tileentity.TileEntity;

import ruiseki.integrateddynamics.api.block.cable.ICable;
import ruiseki.integrateddynamics.api.path.IPathElement;
import ruiseki.integrateddynamics.core.tileentity.TileMultipartTicking;
import ruiseki.okcore.datastructure.DimPos;

/**
 * Implementation of {@link IPathElement} for {@link TileMultipartTicking}.
 * 
 * @author rubensworks
 */
public class PathElementTile extends PathElementCable {

    private final TileEntity tile;
    private final ICable cable;

    public PathElementTile(TileEntity tile, ICable cable) {
        this.tile = tile;
        this.cable = cable;
    }

    @Override
    protected ICable getCable() {
        return cable;
    }

    @Override
    public DimPos getPosition() {
        return DimPos.of(tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
    }
}
