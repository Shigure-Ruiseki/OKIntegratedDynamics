package ruiseki.integrateddynamics.core.helper;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkCarrier;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.INetworkElementProvider;
import ruiseki.integrateddynamics.api.path.IPathElement;
import ruiseki.integrateddynamics.capability.network.NetworkCarrierConfig;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderConfig;
import ruiseki.integrateddynamics.capability.path.PathElementConfig;
import ruiseki.integrateddynamics.core.network.PartNetwork;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.CapabilityHelpers;

/**
 * Network helper methods.
 * 
 * @author rubensworks
 */
public class NetworkHelpers {

    /**
     * Get the network carrier capability at the given position.
     *
     * @param world The world.
     * @param pos   The position.
     * @return The network carrier capability, or null if not present.
     */
    @SuppressWarnings("unchecked")
    public static INetworkCarrier getNetworkCarrier(IBlockAccess world, BlockPos pos) {
        return CapabilityHelpers.getCapability(world, pos, NetworkCarrierConfig.CAPABILITY)
            .getOrNull();
    }

    /**
     * Get the network element provider capability at the given position.
     *
     * @param world The world.
     * @param pos   The position.
     * @return The network element provider capability, or null if not present.
     */
    @SuppressWarnings("unchecked")
    public static INetworkElementProvider getNetworkElementProvider(IBlockAccess world, BlockPos pos) {
        return CapabilityHelpers.getCapability(world, pos, NetworkElementProviderConfig.CAPABILITY)
            .getOrNull();
    }

    /**
     * Get the network at the given position.
     *
     * @param world The world.
     * @param pos   The position.
     * @return The network, or null if no network or network carrier present.
     */
    @SuppressWarnings("unchecked")
    public static INetwork getNetwork(IBlockAccess world, BlockPos pos) {
        INetworkCarrier networkCarrier = getNetworkCarrier(world, pos);
        if (networkCarrier != null) {
            return networkCarrier.getNetwork();
        }
        return null;
    }

    /**
     * Form a new network starting from the given position.
     * This position should have a {@link IPathElement} capability,
     * otherwise this method will fail silently.
     * This will correctly transfer all passed network elements to this new network.
     *
     * @param world The world.
     * @param pos   The starting position.
     * @return The newly created part network.
     *         Can be null if the starting position did not have a {@link IPathElement} capability.
     */
    public static @Nullable PartNetwork initNetwork(World world, BlockPos pos) {
        IPathElement pathElement = CapabilityHelpers.getCapability(world, pos, PathElementConfig.CAPABILITY, null)
            .getOrNull();
        if (pathElement != null) {
            PartNetwork partNetwork = PartNetwork.initiateNetworkSetup(pathElement);
            partNetwork.initialize();
            return partNetwork;
        }
        return null;
    }

    /**
     * This MUST be called by blocks having the {@link INetworkElementProvider} capability in
     * when a neighbouring block is updated, more specifically when
     * {@link net.minecraft.block.Block#onNeighborBlockChange(World, int, int, int, Block)} is called.
     *
     * @param world         The world in which the neighbour was updated.
     * @param pos           The position of the center block.
     * @param neighborBlock The block type of the neighbour that was updated.
     */
    public static void onElementProviderBlockNeighborChange(World world, BlockPos pos, Block neighborBlock) {
        if (!world.isRemote) {
            INetwork network = getNetwork(world, pos);
            INetworkElementProvider<?> networkElementProvider = getNetworkElementProvider(world, pos);
            for (INetworkElement networkElement : networkElementProvider.createNetworkElements(world, pos)) {
                networkElement.onNeighborBlockChange(network, world, neighborBlock);
            }
        }
    }

}
