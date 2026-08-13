package ruiseki.integratedtunnels.api.world;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.init.IRegistry;

/**
 * A registry for block breaking actions.
 * 
 * @author rubensworks
 */
public interface IBlockBreakHandlerRegistry extends IRegistry {

    /**
     * Add a block breaking handler.
     * Multiple handlers can exist for a block.
     * 
     * @param block       A block.
     * @param breakAction A handler.
     * @return The registered handler.
     */
    public IBlockBreakHandler register(Block block, IBlockBreakHandler breakAction);

    /**
     * @return All registered block breaking handlers.
     */
    public Collection<IBlockBreakHandler> getHandlers();

    /**
     * @param block A block.
     * @return All registered block breaking handlers for the given block.
     */
    public Collection<IBlockBreakHandler> getHandlers(Block block);

    /**
     * Get the first possible block breaking handler for the given block state.
     * 
     * @param blockState The block state.
     * @param world      The world.
     * @param pos        The block position.
     * @param player     The breaking player.
     * @return A block breaking handler or null.
     */
    @Nullable
    public IBlockBreakHandler getHandler(BlockState blockState, World world, BlockPos pos, EntityPlayer player);

}
