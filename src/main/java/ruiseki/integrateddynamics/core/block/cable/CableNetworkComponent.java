package ruiseki.integrateddynamics.core.block.cable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.Level;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.api.block.cable.ICable;
import ruiseki.integrateddynamics.api.block.cable.ICableNetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.network.IPartNetworkElement;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.path.ICablePathElement;
import ruiseki.integrateddynamics.api.tileentity.ITileCable;
import ruiseki.integrateddynamics.api.tileentity.ITileCableNetwork;
import ruiseki.integrateddynamics.block.BlockCable;
import ruiseki.integrateddynamics.capability.partcontainer.PartContainerConfig;
import ruiseki.integrateddynamics.core.helper.CableHelpers;
import ruiseki.integrateddynamics.core.network.PartNetwork;
import ruiseki.integrateddynamics.core.path.CablePathElement;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.ItemStackHelpers;
import ruiseki.okcore.helper.TileHelpers;

/**
 * A component for {@link ICableNetwork}.
 *
 * @author rubensworks
 */
public class CableNetworkComponent<C extends ICableNetwork<IPartNetwork, ICablePathElement>>
    implements ICableNetwork<IPartNetwork, ICablePathElement> {

    protected final C cable;

    public CableNetworkComponent(C cable) {
        this.cable = cable;
    }

    @Override
    public CablePathElement createPathElement(World world, BlockPos blockPos) {
        return new CablePathElement(cable, DimPos.of(world, blockPos));
    }

    protected ITileCable getTile(World world, BlockPos pos) {
        return TileHelpers.getSafeTile(world, pos, ITileCable.class);
    }

    protected ITileCableNetwork getTileNetwork(World world, BlockPos pos) {
        return TileHelpers.getSafeTile(world, pos, ITileCableNetwork.class);
    }

    @Override
    public void initNetwork(World world, BlockPos pos) {
        PartNetwork.initiateNetworkSetup(cable, world, pos)
            .initialize();
    }

    @Override
    public boolean canConnect(World world, BlockPos pos, ICable connector, ForgeDirection side) {
        ITileCable tile = getTile(world, pos);
        return tile != null && tile.canConnect(connector, side);
    }

    @Override
    public void updateConnections(World world, BlockPos pos) {
        ITileCable tile = getTile(world, pos);
        if (tile != null) {
            tile.updateConnections();
            world.markBlockForUpdate(pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @Override
    public void triggerUpdateNeighbourConnections(World world, BlockPos pos) {
        for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
            requestConnectionsUpdate(world, pos.offset(side));
        }
    }

    @Override
    public boolean isConnected(World world, BlockPos pos, ForgeDirection side) {
        ITileCable tile = getTile(world, pos);
        return tile != null && tile.isConnected(side);
    }

    @Override
    public void disconnect(World world, BlockPos pos, ForgeDirection side) {
        ITileCable tile = getTile(world, pos);
        if (tile != null) {
            tile.disconnect(side);
        }
    }

    @Override
    public void reconnect(World world, BlockPos pos, ForgeDirection side) {
        ITileCable tile = getTile(world, pos);
        if (tile != null) {
            tile.reconnect(side);
        }
    }

    @Override
    public void remove(World world, BlockPos pos, EntityPlayer player) {
        // world.destroyBlock(pos, true); // We don't call this directly because we don't want breaking sounds to play
        if (!player.capabilities.isCreativeMode) {
            ItemStackHelpers.spawnItemStackToPlayer(world, pos, new ItemStack(BlockCable.getInstance()), player);
        }
        world.setBlockToAir(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public void resetCurrentNetwork(World world, BlockPos pos) {
        ITileCableNetwork tile = getTileNetwork(world, pos);
        if (tile != null) {
            tile.resetCurrentNetwork();
        }
    }

    @Override
    public void setNetwork(IPartNetwork network, World world, BlockPos pos) {
        ITileCableNetwork tile = getTileNetwork(world, pos);
        if (tile != null) {
            if (network != null && tile.getNetwork() != null) {
                IntegratedDynamics
                    .clog(Level.WARN, "Tried to set a new network for a tile without the previous one being removed.");
            }
            tile.setNetwork(network);
        }
    }

    @Override
    public IPartNetwork getNetwork(World world, BlockPos pos) {
        ITileCableNetwork tile = getTileNetwork(world, pos);
        if (tile != null) {
            return tile.getNetwork();
        }
        return null;
    }

    /**
     * Request to update the cable connections at the given position.
     *
     * @param world The world.
     * @param pos   The position of this block.
     */
    public static void requestConnectionsUpdate(World world, BlockPos pos) {
        ICable cable = CableHelpers.getInterface(world, pos, ICable.class);
        if (cable != null) {
            cable.updateConnections(world, pos);
        }
    }

    /**
     * Add this block to a network.
     *
     * @param world The world.
     * @param pos   The position.
     */
    public void addToNetwork(World world, BlockPos pos) {
        triggerUpdateNeighbourConnections(world, pos);
        if (!world.isRemote) {
            initNetwork(world, pos);
        }
    }

    /**
     * Remove this block from its current network.
     *
     * @param world The world.
     * @param pos   The position.
     * @return If the cable was removed.
     */
    public boolean removeCableFromNetwork(World world, BlockPos pos) {
        return removeCableFromNetwork(world, pos, true) && removeCableFromNetwork(world, pos, false);
    }

    /**
     * Remove this block from its current network.
     *
     * @param world      The world.
     * @param pos        The position.
     * @param preDestroy At which stage of the block destruction this is being called.
     * @return If the cable was removed from the network.
     */
    public boolean removeCableFromNetwork(World world, BlockPos pos, boolean preDestroy) {
        if (preDestroy) {
            // Remove the cable from this network if it exists
            IPartNetwork network = getNetwork(world, pos);
            if (network != null) {
                return network.removeCable(cable, createPathElement(world, pos));
            }
        } else {
            triggerUpdateNeighbourConnections(world, pos);
            // Reinit neighbouring networks.
            for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                if (!world.isRemote) {
                    BlockPos sidePos = pos.offset(side);
                    ICableNetwork sideCable = CableHelpers.getInterface(world, sidePos, ICableNetwork.class);
                    if (sideCable != null) {
                        ((ICableNetwork<IPartNetwork, ICablePathElement>) sideCable).initNetwork(world, sidePos);
                    }
                }
            }
            setNetwork(null, world, pos);
        }
        return true;
    }

    /**
     * Remove a single part from the current network.
     * The part is at this stage already removed from the part container.
     *
     * @param world   The world.
     * @param pos     The position.
     * @param network The network
     * @param side    The side to remove the part for.
     * @param removed The part that is already removed.
     * @return If the part was removed from the network.
     */
    public static boolean removePartFromNetwork(World world, BlockPos pos, IPartNetwork network, ForgeDirection side,
        IPartType<?, ?> removed) {
        return removePartFromNetwork(world, pos, true, network, side, removed)
            && removePartFromNetwork(world, pos, false, network, side, removed);
    }

    /**
     * Remove a single part from the current network.
     * The part is at this stage already removed from the part container.
     *
     * @param world      The world.
     * @param pos        The position.
     * @param preDestroy At which stage of the block destruction this is being called.
     * @param network    The network
     * @param side       The side to remove the part for.
     * @param removed    The part that is already removed.
     * @return If the part was removed from the network.
     */
    public static boolean removePartFromNetwork(World world, BlockPos pos, boolean preDestroy, IPartNetwork network,
        ForgeDirection side, IPartType<?, ?> removed) {
        if (preDestroy) {
            // Remove the cable from this network if it exists
            IPartContainer partContainer = PartContainerConfig.get(world, pos);
            if (partContainer != null && network != null) {
                IPartNetworkElement<?, ?> networkElement = (IPartNetworkElement<?, ?>) removed
                    .createNetworkElement(partContainer, DimPos.of(world, pos), side);
                networkElement.onPreRemoved(network);
                if (network.removeNetworkElementPre(networkElement)) {
                    network.removeNetworkElementPost(networkElement);
                    networkElement.onPostRemoved(network);
                    network.notifyPartsChanged();
                    return true;
                }
                return false;
            }
        } else {
            // Reinit neighbour networks.
            BlockPos sidePos = pos.offset(side);
            requestConnectionsUpdate(world, sidePos);
            if (!world.isRemote) {
                ICableNetwork sideCable = CableHelpers.getInterface(world, sidePos, ICableNetwork.class);
                if (sideCable != null) {
                    ((ICableNetwork<IPartNetwork, ICablePathElement>) sideCable).initNetwork(world, sidePos);
                }
            }
        }
        return true;
    }

    /**
     * Called before this block is destroyed.
     *
     * @param world The world.
     * @param pos   The position.
     * @return If the cable can be removed.
     */
    public boolean onPreBlockDestroyed(World world, BlockPos pos) {
        if (!world.isRemote) {
            return removeCableFromNetwork(world, pos, true);
        }
        return true;
    }

    /**
     * Called before after block is destroyed.
     *
     * @param world The world.
     * @param pos   The position.
     * @return If the cable was removed.
     */
    public boolean onPostBlockDestroyed(World world, BlockPos pos) {
        if (!world.isRemote) {
            return removeCableFromNetwork(world, pos, false);
        }
        return true;
    }

    /**
     * Check if one side of a cable can connect.
     * To be used when the cable connections are being updated.
     *
     * @param world       The world.
     * @param pos         The center position.
     * @param side        The side from the center position to check.
     * @param originCable The cable at the center position.
     * @return If it can connect.
     */
    public static boolean canSideConnect(World world, BlockPos pos, ForgeDirection side, ICable originCable) {
        BlockPos neighbourPos = pos.offset(side);
        ICable neighbourCable = CableHelpers.getInterface(world, neighbourPos, ICable.class);
        return neighbourCable != null
            && neighbourCable.canConnect(world, neighbourPos, originCable, side.getOpposite());
    }

}
