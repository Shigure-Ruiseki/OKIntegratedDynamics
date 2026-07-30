package ruiseki.integrateddynamics.api.block;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.okcore.datastructure.BlockPos;

/**
 * A block that can have its light level updated and stored.
 * 
 * @author rubensworks
 */
public interface IDynamicLightBlock {

    /**
     * Set the light level.
     * 
     * @param world The world.
     * @param pos   The position.
     * @param side  The side.
     * @param level The redstone level.
     */
    public void setLightLevel(IBlockAccess world, BlockPos pos, ForgeDirection side, int level);

    /**
     * Get the light level.
     * 
     * @param world The world.
     * @param pos   The position.
     * @param side  The side.
     * @return The redstone level.
     */
    public int getLightLevel(IBlockAccess world, BlockPos pos, ForgeDirection side);

}
