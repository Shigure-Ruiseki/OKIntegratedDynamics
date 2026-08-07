package ruiseki.integrateddynamics.capability;

import net.minecraft.world.World;

import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartContainerFacade;
import ruiseki.integrateddynamics.core.tileentity.TileMultipartTicking;
import ruiseki.okcore.datastructure.BlockPos;

/**
 * Implementation of an {@link IPartContainer} for a tile entity.
 * 
 * @author rubensworks
 */
public class TileMultipartTickingPartContainer extends DefaultPartContainer {

    private final TileMultipartTicking tile;

    public TileMultipartTickingPartContainer(TileMultipartTicking tile) {
        this.tile = tile;
    }

    protected TileMultipartTicking getTile() {
        return tile;
    }

    @Override
    protected void markDirty() {
        getTile().markDirty();
    }

    @Override
    protected void sendUpdate() {
        getTile().sendUpdate();
    }

    @Override
    protected World getWorld() {
        return getTile().getWorldObj();
    }

    @Override
    protected BlockPos getPos() {
        return getTile().getPos();
    }

    @Override
    protected IPartNetwork getNetwork() {
        return getTile().getNetwork();
    }

    @Override
    protected IPartContainerFacade getPartContainerFacade() {
        return (IPartContainerFacade) getTile().getBlock();
    }
}
