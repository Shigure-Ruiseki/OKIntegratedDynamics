package ruiseki.integrateddynamics.block.collidable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.block.BlockCable;
import ruiseki.integrateddynamics.core.helper.CableHelpers;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.okcore.datastructure.BlockPos;

public class CollidableComponentCableConnections extends CollidableComponentCableCenter {

    private AxisAlignedBB getCableBoundingBoxWithPart(World world, BlockPos pos, ForgeDirection side) {
        if (side == null) {
            return BlockCable.CABLE_CENTER_BOUNDINGBOX;
        } else {
            IPartContainer partContainer = PartHelpers.getPartContainer(world, pos);
            return partContainer != null ? partContainer.getPart(side)
                .getPartRenderPosition()
                .getSidedCableBoundingBox(side) : null;
        }
    }

    @Override
    public Collection<ForgeDirection> getPossiblePositions() {
        return Arrays.asList(ForgeDirection.VALID_DIRECTIONS);
    }

    @Override
    public boolean isActive(BlockCable block, World world, BlockPos pos, ForgeDirection position) {
        return super.isActive(block, world, pos, position)
            && (CableHelpers.isCableConnected(world, pos, position) || PartHelpers.getPartContainer(world, pos)
                .hasPart(position));
    }

    @Override
    public List<AxisAlignedBB> getBounds(BlockCable block, World world, BlockPos pos, ForgeDirection position) {
        return Collections.singletonList(
            CableHelpers.isCableConnected(world, pos, position) ? block.getCableBoundingBox(position)
                : getCableBoundingBoxWithPart(world, pos, position));
    }
}
