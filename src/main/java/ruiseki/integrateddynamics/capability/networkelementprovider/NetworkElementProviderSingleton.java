package ruiseki.integrateddynamics.capability.networkelementprovider;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.world.World;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.INetworkElementProvider;
import ruiseki.okcore.datastructure.BlockPos;

/**
 * A network element provider for a single element.
 *
 * @author rubensworks
 */
public abstract class NetworkElementProviderSingleton<N extends INetwork> implements INetworkElementProvider<N> {

    @Override
    public Collection<INetworkElement<N>> createNetworkElements(World world, BlockPos blockPos) {
        return Collections.singleton(createNetworkElement(world, blockPos));
    }

    public abstract INetworkElement<N> createNetworkElement(World world, BlockPos blockPos);
}
