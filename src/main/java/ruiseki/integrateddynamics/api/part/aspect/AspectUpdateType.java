package ruiseki.integrateddynamics.api.part.aspect;

/**
 * Different types of aspect update triggers.
 * I.e., when {@link IAspect#update(IPartNetwork, IPartType, PartTarget, IPartState)} should be called.
 * 
 * @author rubensworks
 */
public enum AspectUpdateType {
    /**
     * Update per network tick.
     */
    NETWORK_TICK,
    /**
     * Update its value on block neigbour changes,
     * i.e., if {@link net.minecraft.block.Block#onNeighborChange(IBlockAccess, BlockPos, BlockPos)} or
     * {@link Block#onNeighborChange(IBlockAccess, BlockPos, BlockPos)} is called.
     */
    BLOCK_UPDATE,
    /**
     * If the update method should never be called.
     */
    NEVER
}
