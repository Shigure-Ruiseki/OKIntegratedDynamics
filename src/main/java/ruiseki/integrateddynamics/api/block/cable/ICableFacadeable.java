package ruiseki.integrateddynamics.api.block.cable;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

import ruiseki.integrateddynamics.api.path.IPathElement;
import ruiseki.okcore.datastructure.BlockPos;

/**
 * Interface for cables that support facades.
 * 
 * @author rubensworks
 */
public interface ICableFacadeable<E extends IPathElement<E>> extends ICable<E> {

    /**
     * @param world The world.
     * @param pos   The position of this block.
     * @return If this container has a facade.
     */
    public boolean hasFacade(IBlockAccess world, BlockPos pos);

    /**
     * @param world The world.
     * @param pos   The position of this block.
     * @return The blockstate of the facade.
     */
    public BlockState getFacade(World world, BlockPos pos);

    /**
     * Set the new facade
     * 
     * @param world      The world.
     * @param pos        The position of this block.
     * @param blockState The new facade or null.
     */
    public void setFacade(World world, BlockPos pos, @Nullable BlockState blockState);

}
