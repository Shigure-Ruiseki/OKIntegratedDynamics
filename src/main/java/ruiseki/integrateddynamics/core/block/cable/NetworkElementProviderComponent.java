package ruiseki.integrateddynamics.core.block.cable;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.INetworkElementProvider;
import ruiseki.integrateddynamics.capability.NetworkElementProviderConfig;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.InventoryHelpers;

/**
 * Component for helping {@link INetworkElementProvider} instances.
 *
 * @author rubensworks
 */
public class NetworkElementProviderComponent<N extends INetwork> {

    @SuppressWarnings("unchecked")
    protected INetworkElementProvider<N> getNetworkElementProvider(World world, BlockPos pos) {
        return (INetworkElementProvider<N>) CapabilityHelpers
            .getCapability(world, pos, NetworkElementProviderConfig.CAPABILITY, null)
            .getOrNull();
    }

    /**
     * Called before this block is destroyed.
     *
     * @param network         The network. Null if this element is part of a corrupted network, should not happen
     *                        though.
     * @param world           The world.
     * @param pos             The position.
     * @param dropMainElement If the main part element should be dropped.
     */
    public void onPreBlockDestroyed(@Nullable N network, World world, BlockPos pos, boolean dropMainElement) {
        // Drop all parts types as item.
        if (!world.isRemote) {
            List<ItemStack> itemStacks = Lists.newLinkedList();
            INetworkElementProvider<N> networkElementProvider = getNetworkElementProvider(world, pos);
            for (INetworkElement<N> networkElement : networkElementProvider.createNetworkElements(world, pos)) {
                networkElement.addDrops(itemStacks, dropMainElement);
                if (network != null) {
                    networkElement.onPreRemoved(network);
                    network.removeNetworkElementPre(networkElement);
                    network.removeNetworkElementPost(networkElement);
                    networkElement.onPostRemoved(network);
                }
            }
            for (ItemStack itemStack : itemStacks) {
                InventoryHelpers.dropItems(world, itemStack, pos);
            }
        }
    }

    /**
     * Called when a neighbouring block is updated, more specifically when
     * {@link net.minecraft.block.Block#onNeighborBlockChange(World, int, int, int, Block)} is called.
     *
     * @param network       The network to update in.
     * @param world         The world in which the neighbour was updated.
     * @param pos           The position of the center block.
     * @param neighborBlock The block type of the neighbour that was updated.
     */
    public void onBlockNeighborChange(@Nullable N network, World world, BlockPos pos, Block neighborBlock) {
        if (!world.isRemote) {
            INetworkElementProvider<N> networkElementProvider = getNetworkElementProvider(world, pos);
            for (INetworkElement<N> networkElement : networkElementProvider.createNetworkElements(world, pos)) {
                networkElement.onNeighborBlockChange(network, world, neighborBlock);
            }
        }
    }

}
