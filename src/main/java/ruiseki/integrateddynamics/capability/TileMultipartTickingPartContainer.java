package ruiseki.integrateddynamics.capability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.block.BlockCable;
import ruiseki.integrateddynamics.core.tileentity.TileMultipartTicking;
import ruiseki.okcore.block.collidable.ICollidable;
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

    @Nullable
    @Override
    public ForgeDirection getWatchingSide(World world, BlockPos pos, EntityPlayer player) {
        ICollidable.RayTraceResult<ForgeDirection> rayTraceResult = ((BlockCable) pos.getBlock(world))
            .doRayTrace(world, pos, player);
        if (rayTraceResult != null) {
            return rayTraceResult.getPositionHit();
        }
        return null;
    }
}
