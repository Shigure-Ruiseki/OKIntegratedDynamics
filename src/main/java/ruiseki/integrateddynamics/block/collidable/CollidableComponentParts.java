package ruiseki.integrateddynamics.block.collidable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.block.BlockCable;
import ruiseki.integrateddynamics.core.helper.PartHelpers;
import ruiseki.okcore.block.collidable.ICollidable;
import ruiseki.okcore.datastructure.BlockPos;

public class CollidableComponentParts implements ICollidable.IComponent<ForgeDirection, BlockCable> {

    protected AxisAlignedBB getPartBoundingBox(World world, BlockPos pos, ForgeDirection side) {
        IPartContainer partContainer = PartHelpers.getPartContainer(world, pos);
        return partContainer != null ? partContainer.getPart(side)
            .getPartRenderPosition()
            .getBoundingBox(side) : null;
    }

    @Override
    public Collection<ForgeDirection> getPossiblePositions() {
        return Arrays.asList(ForgeDirection.VALID_DIRECTIONS);
    }

    @Override
    public int getBoundsCount(ForgeDirection position) {
        return 1;
    }

    @Override
    public boolean isActive(BlockCable cable, World world, BlockPos pos, ForgeDirection position) {
        IPartContainer partContainer = PartHelpers.getPartContainer(world, pos);
        return partContainer.hasPart(position);
    }

    @Override
    public List<AxisAlignedBB> getBounds(BlockCable cable, World world, BlockPos pos, ForgeDirection position) {
        return Collections.singletonList(getPartBoundingBox(world, pos, position));
    }

    @Override
    public ItemStack getPickBlock(World world, BlockPos pos, ForgeDirection position) {
        IPartContainer partContainer = PartHelpers.getPartContainer(world, pos);
        return partContainer.getPart(position)
            .getPickBlock(world, pos, partContainer.getPartState(position));
    }

    @Override
    public boolean destroy(World world, BlockPos pos, ForgeDirection position, EntityPlayer player, boolean b) {
        if (!world.isRemote) {
            return PartHelpers.removePart(world, pos, position, player, b, true);
        }
        return false;
    }
}
