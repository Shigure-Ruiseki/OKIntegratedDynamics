package ruiseki.integrateddynamics.api.network;

import java.util.Collection;

import net.minecraft.world.World;

import ruiseki.okcore.datastructure.BlockPos;

/**
 * Capability that can create instances of an {@link INetworkElement}.
 *
 * @author rubensworks
 */
public interface INetworkElementProvider<N extends INetwork> {

    /**
     * Create network element instances for the given position.
     *
     * @param world    The world.
     * @param blockPos The position.
     * @return A collection of all network elements at this position.
     */
    public Collection<INetworkElement<N>> createNetworkElements(World world, BlockPos blockPos);

}
