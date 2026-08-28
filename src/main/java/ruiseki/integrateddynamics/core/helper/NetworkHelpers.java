package ruiseki.integrateddynamics.core.helper;

import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.network.IEnergyNetwork;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkCarrier;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.INetworkElementProvider;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.path.IPathElement;
import ruiseki.integrateddynamics.capability.network.EnergyNetworkConfig;
import ruiseki.integrateddynamics.capability.network.NetworkCarrierConfig;
import ruiseki.integrateddynamics.capability.network.PartNetworkConfig;
import ruiseki.integrateddynamics.capability.network.PositionedAddonsNetworkIngredientsHandlerConfig;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderConfig;
import ruiseki.integrateddynamics.capability.path.PathElementConfig;
import ruiseki.integrateddynamics.capability.path.SidedPathElement;
import ruiseki.integrateddynamics.core.network.Network;
import ruiseki.integrateddynamics.core.persist.world.NetworkWorldStorage;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.LazyOptional;
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
    @Nullable
    public static LazyOptional<INetworkCarrier> getNetworkCarrier(IBlockAccess world, BlockPos pos,
        @Nullable ForgeDirection side) {
        return CapabilityHelpers.getCapability(world, pos, NetworkCarrierConfig.CAPABILITY, side);
    }

    /**
     * Get the network element provider capability at the given position.
     *
     * @param world The world.
     * @param pos   The position.
     * @param side  The side.
     * @return The network element provider capability, or null if not present.
     */
    @Nullable
    public static LazyOptional<INetworkElementProvider> getNetworkElementProvider(IBlockAccess world, BlockPos pos,
        @Nullable ForgeDirection side) {
        return CapabilityHelpers.getCapability(world, pos, NetworkElementProviderConfig.CAPABILITY, side);
    }

    /**
     * Get the network at the given position.
     *
     * @param world The world.
     * @param pos   The position.
     * @param side  The side.
     * @return The network, or null if no network or network carrier present.
     */
    public static LazyOptional<INetwork> getNetwork(IBlockAccess world, BlockPos pos, @Nullable ForgeDirection side) {
        LazyOptional<LazyOptional<INetwork>> networkCarried = getNetworkCarrier(world, pos, side).lazyMap(carrier -> {
            INetwork network = carrier.getNetwork();
            return network != null ? LazyOptional.of(() -> network) : LazyOptional.empty();
        });
        return networkCarried.orElse(LazyOptional.empty());
    }

    /**
     * Get the network at the given position.
     *
     * @param pos The position.
     * @return The network, or null if no network or network carrier present.
     */
    public static LazyOptional<INetwork> getNetwork(PartPos pos) {
        return getNetwork(
            pos.getPos()
                .getWorld(),
            pos.getPos()
                .getBlockPos(),
            pos.getSide());
    }

    /**
     * Get the network at the given position.
     * If it is not present, then an illegal state exception will be thrown.
     *
     * This should only be called if you know for certain that there will be a network present.
     *
     * @param world The world.
     * @param pos   The position.
     * @param side  The side.
     * @return The network.
     */
    public static INetwork getNetworkChecked(IBlockAccess world, BlockPos pos, @Nullable ForgeDirection side) {
        return getNetwork(world, pos, side)
            .orElseThrow(() -> new IllegalStateException("Could not find a network container at " + pos.toString()));
    }

    /**
     * Get the network at the given position.
     * If it is not present, then an illegal state exception will be thrown.
     *
     * This should only be called if you know for certain that there will be a network present.
     *
     * @param pos The position.
     * @return The network.
     */
    public static INetwork getNetworkChecked(PartPos pos) {
        return getNetwork(pos)
            .orElseThrow(() -> new IllegalStateException("Could not find a network container at " + pos.toString()));
    }

    /**
     * Get the part network capability of a network.
     *
     * @param optionalNetwork The optional network.
     * @return The optional part network.
     */
    public static LazyOptional<IPartNetwork> getPartNetwork(LazyOptional<INetwork> optionalNetwork) {
        return optionalNetwork.map(network -> network.getCapability(PartNetworkConfig.CAPABILITY))
            .orElse(LazyOptional.empty());
    }

    /**
     * Get the part network capability of a network.
     *
     * @param network The network.
     * @return The optional part network.
     */
    public static LazyOptional<IPartNetwork> getPartNetwork(@Nullable INetwork network) {
        if (network == null) {
            return LazyOptional.empty();
        }
        return network.getCapability(PartNetworkConfig.CAPABILITY);
    }

    /**
     * Get the part network capability of a network.
     * If it is not present, then an illegal state exception will be thrown.
     *
     * This should only be called if you know for certain that there will be a part network present.
     *
     * @param network The network.
     * @return The part network.
     */
    public static IPartNetwork getPartNetworkChecked(INetwork network) {
        return network.getCapability(PartNetworkConfig.CAPABILITY)
            .orElseThrow(() -> new IllegalStateException("Could not find a network's part network"));
    }

    /**
     * Get the part network capability of a network.
     *
     * @param optionalNetwork The optional network.
     * @return The optional energy network.
     */
    public static LazyOptional<IEnergyNetwork> getEnergyNetwork(LazyOptional<INetwork> optionalNetwork) {
        return optionalNetwork.map(network -> network.getCapability(EnergyNetworkConfig.CAPABILITY))
            .orElse(LazyOptional.empty());
    }

    /**
     * Get the part network capability of a network.
     *
     * @param network The network.
     * @return The optional energy network.
     */
    public static LazyOptional<IEnergyNetwork> getEnergyNetwork(@Nullable INetwork network) {
        if (network == null) {
            return LazyOptional.empty();
        }
        return network.getCapability(EnergyNetworkConfig.CAPABILITY);
    }

    /**
     * Get the part network capability of a network.
     *
     * @param network The network.
     * @return The energy network.
     */
    public static IEnergyNetwork getEnergyNetworkChecked(INetwork network) {
        return network.getCapability(EnergyNetworkConfig.CAPABILITY)
            .orElseThrow(() -> new IllegalStateException("Could not find a network's energy network"));
    }

    /**
     * Get the ingredient network within a network.
     *
     * @param optionalNetwork     The optional network.
     * @param ingredientComponent The ingredient component type.
     * @param <T>                 The instance type.
     * @param <M>                 The matching condition parameter.
     * @return The optional ingredient network.
     */
    public static <T, M> LazyOptional<IPositionedAddonsNetworkIngredients<T, M>> getIngredientNetwork(
        LazyOptional<INetwork> optionalNetwork, IngredientComponent<T, M> ingredientComponent) {
        return optionalNetwork
            .map(
                network -> ingredientComponent.getCapability(PositionedAddonsNetworkIngredientsHandlerConfig.CAPABILITY)
                    .map(handler -> handler.getStorage(network))
                    .orElse(LazyOptional.empty()))
            .orElse(LazyOptional.empty());
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
     * @return The optionally created part network.
     *         Can be absent if the starting position did not have a {@link IPathElement} capability.
     */
    public static Optional<INetwork> initNetwork(World world, BlockPos pos, @Nullable ForgeDirection side) {
        return CapabilityHelpers.getCapability(world, pos, PathElementConfig.CAPABILITY, side)
            .map(pathElement -> {
                Network network = Network.initiateNetworkSetup(SidedPathElement.of(pathElement, side));
                network.initialize();
                return Optional.<INetwork>of(network);
            })
            .orElse(Optional.empty());
    }

    /**
     * This MUST be called by blocks having the {@link INetworkElementProvider} capability in
     * when a neighbouring block is updated, more specifically when
     * {@link net.minecraft.block.Block#onNeighborChange(IBlockAccess, int, int, int, int, int, int)},
     * {@link Block#onNeighborBlockChange(World, int, int, int, Block)}
     *
     * @param world          The world in which the neighbour was updated.
     * @param pos            The position of the center block.
     * @param side           The side at the center block.
     * @param neighbourBlock The block type of the neighbour that was updated.
     */
    public static void onElementProviderBlockNeighborChange(World world, BlockPos pos, Block neighbourBlock,
        @Nullable ForgeDirection side) {
        if (!world.isRemote) {
            getNetwork(world, pos, side).ifPresent(network -> {
                getNetworkElementProvider(world, pos, side).ifPresent(networkElementProvider -> {
                    for (INetworkElement networkElement : networkElementProvider.createNetworkElements(world, pos)) {
                        networkElement.onNeighborBlockChange(network, world, neighbourBlock, null);
                    }
                });
            });
        }
    }

    /**
     * This MUST be called by blocks having the {@link INetworkElementProvider} capability in
     * when a neighbouring block is updated, more specifically when
     * {@link net.minecraft.block.Block#onNeighborChange(IBlockAccess, int, int, int, int, int, int)},
     * {@link Block#onNeighborBlockChange(World, int, int, int, Block)}
     *
     * @param world             The world in which the neighbour was updated.
     * @param pos               The position of the center block.
     * @param side              The side at the center block.
     * @param neighbourBlock    The block type of the neighbour that was updated.
     * @param neighbourBlockPos The position of the neighbour that was updated.
     */
    public static void onElementProviderBlockNeighborChange(World world, BlockPos pos, Block neighbourBlock,
        @Nullable ForgeDirection side, BlockPos neighbourBlockPos) {
        if (!world.isRemote) {
            getNetwork(world, pos, side).ifPresent(network -> {
                getNetworkElementProvider(world, pos, side).ifPresent(networkElementProvider -> {
                    for (INetworkElement networkElement : networkElementProvider.createNetworkElements(world, pos)) {
                        networkElement.onNeighborBlockChange(network, world, neighbourBlock, null);
                    }
                });
            });
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
     * Warning: this assumes unsided network carrier capabilities, for example full-block network elements.
     *
     * @param world The world.
     * @param pos   The position.
     * @param tile  The tile entity that is unloaded.
     */
    public static void invalidateNetworkElements(World world, BlockPos pos, TileEntity tile) {
        CapabilityHelpers.getCapability(tile, NetworkCarrierConfig.CAPABILITY, null)
            .ifPresent(networkCarrier -> {
                INetwork network = networkCarrier.getNetwork();
                if (network != null) {
                    CapabilityHelpers.getCapability(tile, NetworkElementProviderConfig.CAPABILITY, null)
                        .ifPresent(networkElementProvider -> {
                            for (INetworkElement networkElement : networkElementProvider
                                .createNetworkElements(world, pos)) {
                                networkElement.invalidate(network);
                            }
                        });
                }
            });
    }

    /**
     * Revalidate all network elements at the given position.
     * Warning: this assumes unsided network carrier capabilities, for example full-block network elements.
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
            CapabilityHelpers.getCapability(world, pos, NetworkElementProviderConfig.CAPABILITY)
                .ifPresent(networkElementProvider -> {
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
                });
        }
    }

}
