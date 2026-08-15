package ruiseki.integrateddynamics.capability.cable;

import ruiseki.integrateddynamics.api.block.cable.ICableFakeable;
import ruiseki.integrateddynamics.core.tileentity.TileMultipartTicking;

/**
 * Implementation of {@link ICableFakeable} for {@link TileMultipartTicking}.
 * 
 * @author rubensworks
 */
public class CableFakeableMultipartTicking extends CableFakeableDefault {

    private final TileMultipartTicking tile;

    public CableFakeableMultipartTicking(TileMultipartTicking tile) {
        this.tile = tile;
    }

    @Override
    protected void sendUpdate() {
        tile.sendUpdate();
    }
}
