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

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.integrateddynamics.api.block.IFacadeable;
import ruiseki.integrateddynamics.block.BlockCable;
import ruiseki.integrateddynamics.capability.facadeable.FacadeableConfig;
import ruiseki.integrateddynamics.core.helper.CableHelpers;
import ruiseki.integrateddynamics.item.ItemFacade;
import ruiseki.integrateddynamics.item.ItemFacadeConfig;
import ruiseki.okcore.block.collidable.ICollidable;
import ruiseki.okcore.block.collidable.ImmutableAxisAlignedBB;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.ItemHelpers;

public class CollidableComponentFacade implements ICollidable.IComponent<ForgeDirection, BlockCable> {

    private final AxisAlignedBB BOUNDS = ImmutableAxisAlignedBB.fromBounds(0.01, 0.01, 0.01, 0.99, 0.99, 0.99);

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
        return CableHelpers.hasFacade(world, pos);
    }

    @Override
    public List<AxisAlignedBB> getBounds(BlockCable cable, World world, BlockPos blockPos, ForgeDirection direction) {
        return Collections.singletonList(BOUNDS);
    }

    @Override
    public ItemStack getPickBlock(World world, BlockPos pos, ForgeDirection direction) {
        ItemStack itemStack = new ItemStack(ItemFacadeConfig._instance.getInstance());
        ((ItemFacade) ItemFacadeConfig._instance.getInstance())
            .writeFacadeBlock(itemStack, CableHelpers.getFacade(world, pos));
        return itemStack;
    }

    @Override
    public boolean destroy(World world, BlockPos pos, ForgeDirection direction, EntityPlayer player,
        boolean saveState) {
        if (!world.isRemote) {
            IFacadeable facadeable = CapabilityHelpers.getCapability(world, pos, FacadeableConfig.CAPABILITY, null)
                .getOrNull();
            BlockState blockState = facadeable.getFacade();
            ItemStack itemStack = new ItemStack(ItemFacadeConfig._instance.getInstance());
            ((ItemFacade) ItemFacadeConfig._instance.getInstance()).writeFacadeBlock(itemStack, blockState);
            facadeable.setFacade(null);
            if (!player.capabilities.isCreativeMode) {
                ItemHelpers.spawnItemStackToPlayer(world, pos, itemStack, player);
            }
            return true;
        }
        return false;
    }
}
