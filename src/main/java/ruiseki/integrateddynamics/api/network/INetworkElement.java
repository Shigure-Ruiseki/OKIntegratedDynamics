package ruiseki.integrateddynamics.api.network;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;

/**
 * Objects that can be an element of a {@link INetwork}.
 * Multiple instances for the same 'element' can be created, so the comparator implementation must
 * make sure that these instances are considered equal.
 * These instances are used as a simple way of referring to these elements.
 *
 * @author rubensworks
 */
public interface INetworkElement extends Comparable<INetworkElement> {

    /**
     * @return The tick interval to update this element.
     */
    public int getUpdateInterval();

    /**
     * @return If this element should be updated. This method is only called once during network initialization.
     */
    public boolean isUpdate();

    /**
     * Update at the tick interval specified.
     *
     * @param network The network to update in.
     */
    public void update(INetwork network);

    /**
     * Called right before the network is terminated or will be reset.
     *
     * @param network The network to update in.
     */
    public void beforeNetworkKill(INetwork network);

    /**
     * Called right after this network is initialized.
     *
     * @param network The network to update in.
     */
    public void afterNetworkAlive(INetwork network);

    /**
     * Called right after this network has come alive again,
     * for example after a network restart.
     *
     * @param network The network to update in.
     */
    public void afterNetworkReAlive(INetwork network);

    /**
     * Add the itemstacks to drop when this element is removed.
     *
     * @param itemStacks      The itemstack list to add to.
     * @param dropMainElement If the part itself should also be dropped.
     * @param saveState       If the element state should be saved in the item.
     */
    public void addDrops(List<ItemStack> itemStacks, boolean dropMainElement, boolean saveState);

    /**
     * Called when this element is added to the network.
     *
     * @param network The network.
     * @return If the addition succeeded.
     */
    public boolean onNetworkAddition(INetwork network);

    /**
     * Called when this element is removed from the network.
     *
     * @param network The network.
     */
    public void onNetworkRemoval(INetwork network);

    /**
     * Called when this element is about to be removed.
     * This is called before {@link INetwork#removeNetworkElementPre(INetworkElement)}.
     *
     * @param network The network.
     */
    public void onPreRemoved(INetwork network);

    /**
     * Called when this element has been removed.
     * This is called after {@link INetwork#removeNetworkElementPost(INetworkElement)}.
     *
     * @param network The network.
     */
    public void onPostRemoved(INetwork network);

    /**
     * Called when a neighbouring block is updated, more specifically when
     * {@link net.minecraft.block.Block#onNeighborBlockChange(World, int, int, int, Block)} is called.
     *
     * @param network           The network to update in.
     * @param world             The world in which the neighbour was updated.
     * @param neighbourBlock    block type of the neighbour that was updated.
     * @param neighbourBlockPos The position of the neighbour that was updated.
     */
    public void onNeighborBlockChange(@Nullable INetwork network, IBlockAccess world, Block neighbourBlock,
        BlockPos neighbourBlockPos);

    /**
     * Set the priority and channel of this element in the network.
     *
     * @deprecated Should only be called from {@link INetwork#setPriorityAndChannel(INetworkElement, int, int)}!
     * @param network  The network this element is present in.
     * @param priority The new priority
     * @param channel  The new channel
     */
    @Deprecated
    public void setPriorityAndChannel(INetwork network, int priority, int channel);

    /**
     * @return The priority of this element in the network.
     */
    public int getPriority();

    /**
     * @return The channel of this element in the network.
     */
    public int getChannel();

    /**
     * Invalidate this network element.
     *
     * @param network The network.
     */
    public void invalidate(INetwork network);

    /**
     * Check if this element can be revalidated if it has been invalidated.
     *
     * @param network The network.
     * @return If it can be revalidated.
     */
    public boolean canRevalidate(INetwork network);

    /**
     * Revalidate this network element after it has been invalidated.
     *
     * @param network The network.
     */
    public void revalidate(INetwork network);

    /**
     * @return If this element's position is currently loaded in the world.
     */
    public default boolean isLoaded() {
        return true;
    }

    /**
     * If a network element on the given position should tick.
     * This can be used as implementation for {@link INetworkElement#isLoaded()}.
     *
     * @param pos A position.
     * @return If it should tick.
     */
    public static boolean shouldTick(DimPos pos) {
        return pos.isLoaded();
    }
}
