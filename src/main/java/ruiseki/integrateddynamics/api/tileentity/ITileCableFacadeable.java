package ruiseki.integrateddynamics.api.tileentity;

import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;

/**
 * Interface for tile entities behind block that are a
 * {@link ruiseki.integrateddynamics.api.block.cable.ICableFacadeable}.
 *
 * @author rubensworks
 */
public interface ITileCableFacadeable extends ITileCable {

    /**
     * @return If this container has a facade.
     */
    public boolean hasFacade();

    /**
     * @return The blockstate of the facade.
     */
    public BlockState getFacade();

    /**
     * Set the new facade
     *
     * @param blockState The new facade or null.
     */
    public void setFacade(@Nullable BlockState blockState);

}
