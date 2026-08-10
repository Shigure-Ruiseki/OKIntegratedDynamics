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
import ruiseki.integrateddynamics.core.helper.CableHelpers;
import ruiseki.okcore.block.collidable.ICollidable;
import ruiseki.okcore.datastructure.BlockPos;

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
    public boolean isActive(BlockCable block, World world, BlockPos pos, ForgeDirection position) {
        return CableHelpers.isNoFakeCable(world, pos, position);
    }

    @Override
    public List<AxisAlignedBB> getBounds(BlockCable block, World world, BlockPos pos, ForgeDirection direction) {
        return Collections.singletonList(block.getCableBoundingBox(null));
    }

    @Override
    public ItemStack getPickBlock(World world, BlockPos pos, ForgeDirection direction) {
        return new ItemStack(BlockCable.getInstance());
    }

    @Override
    public boolean destroy(World world, BlockPos pos, ForgeDirection direction, EntityPlayer player,
        boolean saveState) {
        if (!world.isRemote) {
            CableHelpers.removeCable(world, pos, player);
            return true;
        }
        return false;
    }
}
