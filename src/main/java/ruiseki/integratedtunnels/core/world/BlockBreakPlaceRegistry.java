package ruiseki.integratedtunnels.core.world;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;

import ruiseki.integratedtunnels.api.world.IBlockPlaceHandler;
import ruiseki.integratedtunnels.api.world.IBlockPlaceHandlerRegistry;
import ruiseki.okcore.datastructure.BlockPos;

/**
 * Implementation of {@link IBlockPlaceHandlerRegistry}.
 * 
 * @author rubensworks
 */
public class BlockBreakPlaceRegistry implements IBlockPlaceHandlerRegistry {

    private static BlockBreakPlaceRegistry INSTANCE = new BlockBreakPlaceRegistry();

    private final Multimap<Item, IBlockPlaceHandler> handlers = Multimaps
        .newSetMultimap(Maps.<Item, Collection<IBlockPlaceHandler>>newIdentityHashMap(), Sets::newIdentityHashSet);

    private BlockBreakPlaceRegistry() {

    }

    /**
     * @return The unique instance.
     */
    public static BlockBreakPlaceRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public IBlockPlaceHandler register(Item item, IBlockPlaceHandler placeAction) {
        handlers.put(item, placeAction);
        return placeAction;
    }

    @Override
    public Collection<IBlockPlaceHandler> getHandlers() {
        return Collections.unmodifiableCollection(handlers.values());
    }

    @Override
    public Collection<IBlockPlaceHandler> getHandlers(Item item) {
        return Collections.unmodifiableCollection(handlers.get(item));
    }

    @Nullable
    @Override
    public IBlockPlaceHandler getHandler(ItemStack itemStack, World world, BlockPos pos, ForgeDirection side,
        float hitX, float hitY, float hitZ, EntityPlayer player) {
        for (IBlockPlaceHandler placeHandler : getHandlers(itemStack.getItem())) {
            if (placeHandler.shouldApply(itemStack, world, pos, side, hitX, hitY, hitZ, player)) {
                return placeHandler;
            }
        }
        return null;
    }
}
