package ruiseki.integrateddynamics.api.block.cable;

import net.minecraft.world.World;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkCarrier;
import ruiseki.integrateddynamics.api.path.IPathElement;
import ruiseki.okcore.datastructure.BlockPos;

/**
 * Interface for cables that are network-aware.
 * 
 * @author rubensworks
 */
public interface ICableNetwork<N extends INetwork, E extends IPathElement<E>> extends ICable<E>, INetworkCarrier<N> {

    /**
     * (Re-)initialize the network at the given position.
     * 
     * @param world The world.
     * @param pos   The position of this block.
     */
    public void initNetwork(World world, BlockPos pos);

}
