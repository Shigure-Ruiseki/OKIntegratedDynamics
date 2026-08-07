package ruiseki.integrateddynamics.capability.facadeable;

import org.apache.commons.lang3.tuple.Pair;
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
        return tile.getFacadeBlockName() != null && !tile.getFacadeBlockName()
            .isEmpty();
    }

    @Override // TODO: move to helpers
    public BlockState getFacade() {
        if (!hasFacade()) {
            return null;
        }
        return BlockHelpers.deserializeBlockState(Pair.of(tile.getFacadeBlockName(), tile.getFacadeMeta()));
    }

    @Override // TODO: move to helpers
    public void setFacade(@Nullable BlockState blockState) {
        if (blockState == null) {
            tile.setFacadeMeta(0);
            tile.setFacadeBlockName(null);
        } else {
            Pair<String, Integer> serializedBlockState = BlockHelpers.serializeBlockState(blockState);
            tile.setFacadeMeta(serializedBlockState.getRight());
            tile.setFacadeBlockName(serializedBlockState.getLeft());
        }
        tile.sendUpdate();
    }
}
