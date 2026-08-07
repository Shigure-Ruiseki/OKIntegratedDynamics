package ruiseki.integrateddynamics.block.collidable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.block.BlockCable;
import ruiseki.okcore.block.collidable.ICollidable;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.ItemStackHelpers;

public class CollidableComponentCableCenter implements ICollidable.IComponent<ForgeDirection, BlockCable> {

    @Override
    public Collection<ForgeDirection> getPossiblePositions() {
        return Arrays.asList(new ForgeDirection[] { null });
    }

    @Override
    public int getBoundsCount(ForgeDirection position) {
        return 1;
    }

    @Override
    public boolean isActive(BlockCable cable, World world, BlockPos pos, ForgeDirection direction) {
        return cable.isRealCable(world, pos);
    }

    @Override
    public List<AxisAlignedBB> getBounds(BlockCable cable, World world, BlockPos pos, ForgeDirection direction) {
        return Collections.singletonList(cable.getCableBoundingBox(null));
    }

    @Override
    public ItemStack getPickBlock(World world, BlockPos pos, ForgeDirection direction) {
        return new ItemStack(BlockCable.getInstance());
    }

    @Override
    public boolean destroy(World world, BlockPos pos, ForgeDirection direction, EntityPlayer player, boolean b) {
        Block block = pos.getBlock(world);
        if (!world.isRemote) {
            if (block instanceof BlockCable cable) {
                if (cable.getPartContainer(world, pos)
                    .hasParts()) {
                    cable.setRealCable(world, pos, false);
                    if (!player.capabilities.isCreativeMode) {
                        ItemStackHelpers
                            .spawnItemStackToPlayer(world, pos, new ItemStack(BlockCable.getInstance()), player);
                    }
                    return false;
                } else {
                    cable.remove(world, pos, player);
                    return true;
                }
            }
        }
        return false;
    }
}
