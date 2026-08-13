package ruiseki.integratedtunnels.api.world;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.okcore.datastructure.BlockPos;

/**
 * An interface for custom block breaking actions.
 * 
 * @author rubensworks
 */
public interface IBlockBreakHandler {

    /**
     * If this can handle the given block state.
     * 
     * @param blockState The block state.
     * @param world      The world.
     * @param pos        The block position.
     * @param player     The breaking player.
     * @return If this can handle the given block state.
     */
    public boolean shouldApply(BlockState blockState, World world, BlockPos pos, EntityPlayer player);

    /**
     * Get the dropping items of the given block.
     * 
     * @param blockState The block state.
     * @param world      The world.
     * @param pos        The block position.
     * @param player     The breaking player.
     * @return A list of itemstacks where each element must be removable.
     */
    public List<ItemStack> getDrops(BlockState blockState, World world, BlockPos pos, EntityPlayer player);

    /**
     * Break the given block.
     * 
     * @param blockState The block state.
     * @param world      The world.
     * @param pos        The block position.
     * @param player     The breaking player.
     */
    public void breakBlock(BlockState blockState, World world, BlockPos pos, EntityPlayer player);

}
