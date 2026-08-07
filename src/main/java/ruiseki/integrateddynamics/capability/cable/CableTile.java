package ruiseki.integrateddynamics.capability.cable;

import net.minecraft.world.World;

import ruiseki.integrateddynamics.api.block.cable.ICable;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.tileentity.TileEntityOK;

/**
 * Default implementation of {@link ICable}.
 * 
 * @author rubensworks
 */
public abstract class CableTile<T extends TileEntityOK> extends CableDefault {

    protected final T tile;

    public CableTile(T tile) {
        this.tile = tile;
    }

    @Override
    protected void markDirty() {
        tile.markDirty();
    }

    @Override
    protected void sendUpdate() {
        tile.sendUpdate();
    }

    @Override
    protected World getWorld() {
        return tile.getWorldObj();
    }

    @Override
    protected BlockPos getPos() {
        return tile.getPos();
    }
}
