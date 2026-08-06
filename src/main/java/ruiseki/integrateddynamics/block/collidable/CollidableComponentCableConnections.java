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

import ruiseki.integrateddynamics.block.BlockCable;
import ruiseki.okcore.block.collidable.ICollidable;
import ruiseki.okcore.datastructure.BlockPos;

public class CollidableComponentCableConnections implements ICollidable.IComponent<ForgeDirection, BlockCable> {

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
        return BlockCable.CENTER_COMPONENT.isActive(cable, world, pos, position)
            && (cable.isConnected(world, pos, position) || cable.hasPart(world, pos, position));
    }

    @Override
    public List<AxisAlignedBB> getBounds(BlockCable block, World world, BlockPos pos, ForgeDirection position) {
        return Collections.singletonList(
            block.isConnected(world, pos, position) ? block.getCableBoundingBox(position)
                : block.getCableBoundingBoxWithPart(world, pos, position));
    }

    @Override
    public ItemStack getPickBlock(World world, BlockPos pos, ForgeDirection position) {
        return new ItemStack(BlockCable.getInstance());
    }

    @Override
    public boolean destroy(World world, BlockPos pos, ForgeDirection position, EntityPlayer player, boolean saveState) {
        return BlockCable.CENTER_COMPONENT.destroy(world, pos, position, player, saveState);
    }
}
