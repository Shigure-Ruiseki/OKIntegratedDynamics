package ruiseki.integrateddynamics.capability.cable;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.api.block.cable.ICable;
import ruiseki.integrateddynamics.core.tileentity.TileMultipartTicking;
import ruiseki.okcore.datastructure.EnumFacingMap;

/**
 * Default implementation of {@link ICable}.
 * 
 * @author rubensworks
 */
public class CableTileMultipartTicking extends CableTile<TileMultipartTicking> {

    public CableTileMultipartTicking(TileMultipartTicking tile) {
        super(tile);
    }

    @Override
    protected boolean isForceDisconnectable() {
        return true;
    }

    @Override
    protected EnumFacingMap<Boolean> getForceDisconnected() {
        return tile.getForceDisconnected();
    }

    @Override
    protected EnumFacingMap<Boolean> getConnected() {
        return tile.getConnected();
    }

    @Override
    public boolean isForceDisconnected(ForgeDirection side) {
        if (!tile.getCableFakeable()
            .isRealCable() || tile.getPartContainer()
                .hasPart(side))
            return true;
        return super.isForceDisconnected(side);
    }
}
