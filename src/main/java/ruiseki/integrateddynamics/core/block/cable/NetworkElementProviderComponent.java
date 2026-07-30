package ruiseki.integrateddynamics.core.block.cable;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.INetworkElementProvider;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.InventoryHelpers;

/**
 * Component for helping {@link INetworkElementProvider} instances.
 *
 * @author rubensworks
 */
public class NetworkElementProviderComponent<N extends INetwork> {

    private final INetworkElementProvider<N> networkElementProvider;

    public NetworkElementProviderComponent(INetworkElementProvider<N> networkElementProvider) {
        this.networkElementProvider = networkElementProvider;
    }

    /**
     * Called before this block is destroyed.
     *
     * @param network         The network
     * @param world           The world.
     * @param pos             The position.
     * @param dropMainElement If the main part element should be dropped.
     */
    public void onPreBlockDestroyed(N network, World world, BlockPos pos, boolean dropMainElement) {
        // Drop all parts types as item.
        if (!world.isRemote) {
            List<ItemStack> itemStacks = Lists.newLinkedList();
            for (INetworkElement<N> networkElement : networkElementProvider.createNetworkElements(world, pos)) {
                networkElement.addDrops(itemStacks, dropMainElement);
                networkElement.onPreRemoved(network);
                network.removeNetworkElementPre(networkElement);
                network.removeNetworkElementPost(networkElement);
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
    public void onBlockNeighborChange(N network, World world, BlockPos pos, Block neighborBlock) {
        if (!world.isRemote) {
            for (INetworkElement<N> networkElement : networkElementProvider.createNetworkElements(world, pos)) {
                networkElement.onNeighborBlockChange(network, world, neighborBlock);
            }
        }
    }

}
