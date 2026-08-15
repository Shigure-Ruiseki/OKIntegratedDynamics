package ruiseki.integratedtunnels.core.world;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.integratedtunnels.api.world.IBlockBreakHandler;
import ruiseki.integratedtunnels.api.world.IBlockBreakHandlerRegistry;
import ruiseki.okcore.datastructure.BlockPos;

/**
 * Implementation of {@link IBlockBreakHandlerRegistry}.
 * 
 * @author rubensworks
 */
public class BlockBreakHandlerRegistry implements IBlockBreakHandlerRegistry {

    private static BlockBreakHandlerRegistry INSTANCE = new BlockBreakHandlerRegistry();

    private final Multimap<Block, IBlockBreakHandler> handlers = Multimaps
        .newSetMultimap(Maps.<Block, Collection<IBlockBreakHandler>>newIdentityHashMap(), Sets::newIdentityHashSet);

    private BlockBreakHandlerRegistry() {

    }

    /**
     * @return The unique instance.
     */
    public static BlockBreakHandlerRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public IBlockBreakHandler register(Block block, IBlockBreakHandler breakAction) {
        handlers.put(block, breakAction);
        return breakAction;
    }

    @Override
    public Collection<IBlockBreakHandler> getHandlers() {
        return Collections.unmodifiableCollection(handlers.values());
    }

    @Override
    public Collection<IBlockBreakHandler> getHandlers(Block block) {
        return Collections.unmodifiableCollection(handlers.get(block));
    }

    @Nullable
    @Override
    public IBlockBreakHandler getHandler(BlockState blockState, World world, BlockPos pos, EntityPlayer player) {
        for (IBlockBreakHandler breakHandler : getHandlers(blockState.getBlock())) {
            if (breakHandler.shouldApply(blockState, world, pos, player)) {
                return breakHandler;
            }
        }
        return null;
    }
}
