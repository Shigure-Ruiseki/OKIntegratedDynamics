package ruiseki.integrateddynamics.capability.partcontainer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.block.BlockCable;
import ruiseki.integrateddynamics.core.tileentity.TileMultipartTicking;
import ruiseki.okcore.block.collidable.ICollidable;
import ruiseki.okcore.datastructure.BlockPos;

/**
 * Implementation of an {@link IPartContainer} for a part entity.
 *
 * @author rubensworks
 */
public class PartContainerTileMultipartTicking extends PartContainerDefault {

    private final TileMultipartTicking tile;

    public PartContainerTileMultipartTicking(TileMultipartTicking tile) {
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
    protected INetwork getNetwork() {
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
