package ruiseki.integrateddynamics.core.helper;

import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.network.IEnergyNetwork;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkCarrier;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.INetworkElementProvider;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.path.IPathElement;
import ruiseki.integrateddynamics.capability.network.EnergyNetworkConfig;
import ruiseki.integrateddynamics.capability.network.NetworkCarrierConfig;
import ruiseki.integrateddynamics.capability.network.PartNetworkConfig;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderConfig;
import ruiseki.integrateddynamics.capability.path.PathElementConfig;
import ruiseki.integrateddynamics.capability.path.SidedPathElement;
import ruiseki.integrateddynamics.core.network.Network;
import ruiseki.integrateddynamics.core.persist.world.NetworkWorldStorage;
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
     * @param side  The side.
     * @return The network carrier capability, or null if not present.
     */
    public static INetworkCarrier getNetworkCarrier(IBlockAccess world, BlockPos pos, @Nullable ForgeDirection side) {
        return CapabilityHelpers.getCapability(world, pos, NetworkCarrierConfig.CAPABILITY, side)
            .getOrNull();
    }

    /**
     * Get the network element provider capability at the given position.
     *
     * @param world The world.
     * @param pos   The position.
     * @param side  The side.
     * @return The network element provider capability, or null if not present.
     */
    public static INetworkElementProvider getNetworkElementProvider(IBlockAccess world, BlockPos pos,
        @Nullable ForgeDirection side) {
        return CapabilityHelpers.getCapability(world, pos, NetworkElementProviderConfig.CAPABILITY, side)
            .getOrNull();
    }

    /**
     * Get the network at the given position.
     *
     * @param world The world.
     * @param pos   The position.
     * @param side  The side.
     * @return The network, or null if no network or network carrier present.
     */
    public static INetwork getNetwork(IBlockAccess world, BlockPos pos, @Nullable ForgeDirection side) {
        INetworkCarrier networkCarrier = getNetworkCarrier(world, pos, side);
        if (networkCarrier != null) {
            return networkCarrier.getNetwork();
        }
        return null;
    }

    /**
     * Get the part network capability of a network.
     *
     * @param network The network.
     * @return The part network.
     */
    public static IPartNetwork getPartNetwork(@Nullable INetwork network) {
        if (network == null) return null;
        return network.getCapability(PartNetworkConfig.CAPABILITY)
            .getOrNull();
    }

    /**
     * Get the part network capability of a network.
     *
     * @param network The network.
     * @return The part network.
     */
    public static IEnergyNetwork getEnergyNetwork(@Nullable INetwork network) {
        if (network == null) return null;
        return network.getCapability(EnergyNetworkConfig.CAPABILITY)
            .getOrNull();
    }

    /**
     * Form a new network starting from the given position.
     * This position should have a {@link IPathElement} capability,
     * otherwise this method will fail silently.
     * This will correctly transfer all passed network elements to this new network.
     *
     * @param world The world.
     * @param pos   The starting position.
     * @param side  The side.
     * @return The newly created part network.
     *         Can be null if the starting position did not have a {@link IPathElement} capability.
     */
    public static @Nullable INetwork initNetwork(World world, BlockPos pos, @Nullable ForgeDirection side) {
        IPathElement pathElement = CapabilityHelpers.getCapability(world, pos, PathElementConfig.CAPABILITY)
            .getOrNull();
        if (pathElement != null) {
            Network network = Network.initiateNetworkSetup(SidedPathElement.of(pathElement, side));
            network.initialize();
            return network;
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
     * @param side          The side at the center block.
     */
    public static void onElementProviderBlockNeighborChange(World world, BlockPos pos, Block neighborBlock,
        @Nullable ForgeDirection side) {
        if (!world.isRemote) {
            INetwork network = getNetwork(world, pos, side);
            INetworkElementProvider networkElementProvider = getNetworkElementProvider(world, pos, side);
            for (INetworkElement networkElement : networkElementProvider.createNetworkElements(world, pos)) {
                networkElement.onNeighborBlockChange(network, world, neighborBlock);
            }
        }
    }

    /**
     * @return If networks should work and evaluations should be done.
     */
    public static boolean shouldWork() {
        return !GeneralConfig.safeMode;
    }

    /**
     * Invalidate all network elements at the given position.
     *
     * @param world The world.
     * @param pos   The position.
     */
    public static void invalidateNetworkElements(World world, BlockPos pos) {
        INetworkCarrier networkCarrier = CapabilityHelpers.getCapability(world, pos, NetworkCarrierConfig.CAPABILITY)
            .getOrNull();
        if (networkCarrier != null) {
            INetwork network = networkCarrier.getNetwork();
            if (network != null) {
                INetworkElementProvider networkElementProvider = CapabilityHelpers
                    .getCapability(world, pos, NetworkElementProviderConfig.CAPABILITY)
                    .getOrNull();
                if (networkElementProvider != null) {
                    for (INetworkElement networkElement : networkElementProvider.createNetworkElements(world, pos)) {
                        networkElement.invalidate(network);
                    }
                }
            }
        }
    }

    /**
     * Revalidate all network elements at the given position.
     *
     * @param world The world.
     * @param pos   The position.
     */
    public static void revalidateNetworkElements(World world, BlockPos pos) {
        INetworkCarrier networkCarrier = CapabilityHelpers.getCapability(world, pos, NetworkCarrierConfig.CAPABILITY)
            .getOrNull();
        IPathElement pathElement = CapabilityHelpers.getCapability(world, pos, PathElementConfig.CAPABILITY)
            .getOrNull();
        if (networkCarrier != null && pathElement != null && networkCarrier.getNetwork() == null) {
            INetworkElementProvider networkElementProvider = CapabilityHelpers
                .getCapability(world, pos, NetworkElementProviderConfig.CAPABILITY)
                .getOrNull();
            if (networkElementProvider != null) {
                // Attempt to revalidate the network elements in this provider
                for (INetwork network : NetworkWorldStorage.getInstance(IntegratedDynamics._instance)
                    .getNetworks()) {
                    if (network.containsSidedPathElement(SidedPathElement.of(pathElement, null))) {
                        // Revalidate all network elements
                        for (INetworkElement networkElement : networkElementProvider
                            .createNetworkElements(world, pos)) {
                            networkElement.revalidate(network);
                        }
                        break; // No need to check the other networks anymore
                    }
                }
            }
        }
    }
}
