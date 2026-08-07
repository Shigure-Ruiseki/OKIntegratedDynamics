package ruiseki.integrateddynamics.api.part;

import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.capabilities.ICapabilitySerializable;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;

/**
 * An interface for containers that can hold {@link IPartType}s.
 *
 * @author rubensworks
 */
public interface IPartContainer extends ICapabilitySerializable {

    /**
     * Should be called every tick, updates parts.
     */
    public void update();

    /**
     * @return The position this container is at.
     */
    public DimPos getPosition();

    /**
     * @return The parts inside this container.
     */
    public Map<ForgeDirection, IPartType<?, ?>> getParts();

    /**
     * @return If this container has at least one part.
     */
    public boolean hasParts();

    /**
     * Set the part for a side.
     *
     * @param side      The side to place the part on.
     * @param part      The part.
     * @param partState The state for this part.
     * @param <P>       The type of part.
     * @param <S>       The type of part state.
     */
    public <P extends IPartType<P, S>, S extends IPartState<P>> void setPart(ForgeDirection side, IPartType<P, S> part,
        IPartState<P> partState);

    /**
     * Check if the given part can be added at the given side.
     *
     * @param side The side to place the part on.
     * @param part The part.
     * @param <P>  The type of part.
     * @param <S>  The type of part state.
     * @return If the part can be added.
     */
    public <P extends IPartType<P, S>, S extends IPartState<P>> boolean canAddPart(ForgeDirection side,
        IPartType<P, S> part);

    /**
     * Get the part of a side, can be null.
     *
     * @param side The side.
     * @return The part or null.
     */
    public IPartType getPart(ForgeDirection side);

    /**
     * @param side The side.
     * @return If the given side has a part.
     */
    public boolean hasPart(ForgeDirection side);

    /**
     * Remove the part from a side, can return null if there was no part on that side.
     *
     * @param side   The side.
     * @param player The player removing the part.
     * @return The removed part or null.
     */
    public IPartType removePart(ForgeDirection side, @Nullable EntityPlayer player);

    /**
     * dz
     * Set the state of a part.
     *
     * @param side      The side.
     * @param partState The part state.
     */
    public void setPartState(ForgeDirection side, IPartState partState);

    /**
     * Get the state of a part.
     *
     * @param side The side.
     * @return The part state.
     */
    public IPartState getPartState(ForgeDirection side);

    /**
     * Get the part side the player is watching.
     * This is used to determine the part the player is looking at.
     * 
     * @param world  The world.
     * @param pos    The block position to perform a ray trace for.
     * @param player The player.
     * @return The side the player is watching or null.
     */
    public @Nullable ForgeDirection getWatchingSide(World world, BlockPos pos, EntityPlayer player);
}
