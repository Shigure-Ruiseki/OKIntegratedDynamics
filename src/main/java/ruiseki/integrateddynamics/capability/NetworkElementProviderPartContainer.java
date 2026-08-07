package ruiseki.integrateddynamics.capability;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Sets;

import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.INetworkElementProvider;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.IPartContainer;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;

/**
 * Network element provider for {@link IPartContainer}.
 * 
 * @author rubensworks
 */
public class NetworkElementProviderPartContainer implements INetworkElementProvider<IPartNetwork> {

    private final IPartContainer partContainer;

    public NetworkElementProviderPartContainer(IPartContainer partContainer) {
        this.partContainer = partContainer;
    }

    @Override
    public Collection<INetworkElement<IPartNetwork>> createNetworkElements(World world, BlockPos blockPos) {
        Set<INetworkElement<IPartNetwork>> sidedElements = Sets.newHashSet();
        for (Map.Entry<ForgeDirection, IPartType<?, ?>> entry : partContainer.getParts()
            .entrySet()) {
            sidedElements.add(
                entry.getValue()
                    .createNetworkElement(partContainer, DimPos.of(world, blockPos), entry.getKey()));
        }
        return sidedElements;
    }
}
