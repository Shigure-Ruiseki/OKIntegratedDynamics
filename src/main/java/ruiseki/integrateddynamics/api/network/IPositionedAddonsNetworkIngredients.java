package ruiseki.integrateddynamics.api.network;

import java.util.Iterator;

import net.minecraft.tileentity.TileEntity;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorageWrapperHandler;
import ruiseki.commoncapabilities.api.ingredient.storage.IngredientComponentStorageEmpty;
import ruiseki.integrateddynamics.api.ingredient.IIngredientComponentStorageObservable;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.datastructure.DimPos;

/**
 * An ingredient network that can hold prioritized positions.
 * 
 * @param <T> The instance type.
 * @param <M> The matching condition parameter, may be Void. Instances MUST properly implement the equals method.
 * @author rubensworks
 */
public interface IPositionedAddonsNetworkIngredients<T, M>
    extends IPositionedAddonsNetwork, IIngredientComponentStorageObservable<T, M> {

    /**
     * @return The ingredient component type this storage applies to.
     */
    public IngredientComponent<T, M> getComponent();

    /**
     * @return The quantity rate limit for each storage mutation.
     */
    public long getRateLimit();

    /**
     * Get the storage at the given position.
     * 
     * @param pos A position.
     * @return The storage, or an empty storage if none is available.
     */
    public default IIngredientComponentStorage<T, M> getPositionedStorage(PartPos pos) {
        IIngredientComponentStorage<T, M> storage = getPositionedStorageUnsafe(pos);
        return storage == null ? new IngredientComponentStorageEmpty<>(getComponent()) : storage;
    }

    /**
     * Get all instances at the target position.
     * 
     * @param pos A part position.
     * @return A collection of instances. This can not be a view, and must be a deep copy of the target.
     */
    public default Iterator<T> getRawInstances(PartPos pos) {
        return getPositionedStorage(pos).iterator();
    }

    /**
     * Get the storage at the given position.
     * 
     * @param pos A position.
     * @return The storage.
     */
    @Nullable
    public default IIngredientComponentStorage<T, M> getPositionedStorageUnsafe(PartPos pos) {
        DimPos dimPos = pos.getPos();
        TileEntity tile = dimPos.getBlockPos()
            .getTileEntity(dimPos.getWorld());
        return tile != null ? getComponent().getStorage((ICapabilityProvider) tile, pos.getSide()) : null;
    }

    /**
     * Get the storage at the given channel.
     * 
     * @param channel A channel id.
     * @return A storage.
     */
    public IIngredientComponentStorage<T, M> getChannel(int channel);

    /**
     * Get the external storage at the given channel.
     * 
     * @param capability A capability to wrap the channel in.
     * @param channel    A channel id.
     * @param <S>        The external storage type.
     * @return An external storage, or null if no wrapping is possible for the given capability.
     */
    @Nullable
    public default <S> S getChannelExternal(Capability<S> capability, int channel) {
        IIngredientComponentStorageWrapperHandler<T, M, S> wrapperHandler = getComponent()
            .getStorageWrapperHandler(capability);
        return wrapperHandler != null ? wrapperHandler.wrapStorage(getChannel(channel)) : null;
    }

}
