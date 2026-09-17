package ruiseki.integrateddynamics.capability.facadeable;

import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.integrateddynamics.api.block.IFacadeable;
import ruiseki.integrateddynamics.core.tileentity.TileMultipartTicking;
import ruiseki.okcore.helper.BlockHelpers;

/**
 * Default implementation of {@link IFacadeable}.
 *
 * @author rubensworks
 */
public class FacadeableTileMultipartTicking implements IFacadeable {

    private final TileMultipartTicking tile;

    public FacadeableTileMultipartTicking(TileMultipartTicking tile) {
        this.tile = tile;
    }

    @Override
    public boolean hasFacade() {
        return tile.getFacadeBlockTag() != null;
    }

    @Override
    public BlockState getFacade() {
        if (!hasFacade()) {
            return null;
        }
        return BlockHelpers.deserializeBlockState(tile.getFacadeBlockTag());
    }

    @Override
    public void setFacade(@Nullable BlockState blockState) {
        if (blockState == null) {
            tile.setFacadeBlockTag(null);
        } else {
            tile.setFacadeBlockTag(BlockHelpers.serializeBlockState(blockState));
        }
        tile.sendUpdate();
    }
}
