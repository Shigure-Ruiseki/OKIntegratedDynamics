package ruiseki.integrateddynamics.capability.networkelementprovider;

import java.util.Collection;
import java.util.Collections;

import net.minecraft.world.World;

import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.INetworkElementProvider;
import ruiseki.okcore.datastructure.BlockPos;

/**
 * An dummy network element provider implementation.
 *
 * @author rubensworks
 */
public class NetworkElementProviderEmpty implements INetworkElementProvider {

    @Override
    public Collection<INetworkElement> createNetworkElements(World world, BlockPos blockPos) {
        return Collections.emptyList();
    }
}
