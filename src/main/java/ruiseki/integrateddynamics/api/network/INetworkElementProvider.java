package ruiseki.integrateddynamics.api.network;

import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.datastructure.BlockPos;

/**
 * Capability that can create instances of an {@link INetworkElement}.
 * Blocks that provide this capability MUST properly call
 * {@link ruiseki.integrateddynamics.core.helper.NetworkHelpers#onElementProviderBlockNeighborChange(World, BlockPos, Block, ForgeDirection)}
 *
 * @author rubensworks
 */
public interface INetworkElementProvider {

    /**
     * Create network element instances for the given position.
     *
     * @param world    The world.
     * @param blockPos The position.
     * @return A collection of all network elements at this position.
     */
    public Collection<INetworkElement> createNetworkElements(World world, BlockPos blockPos);

}
