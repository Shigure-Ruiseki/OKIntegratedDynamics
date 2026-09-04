package ruiseki.integratedterminals.api.terminalstorage.location;

import java.util.Collection;

import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.init.IRegistry;

/**
 * A registry for {@link ITerminalStorageLocation}.
 *
 * @author rubensworks
 */
public interface ITerminalStorageLocationRegistry extends IRegistry {

    /**
     * Register a new location type.
     *
     * @param location The location to register.
     * @param <L>      The location type.
     * @param <T>      The location type type.
     * @return The registered location.
     */
    public <L, T extends ITerminalStorageLocation<L>> T register(T location);

    /**
     * Get a location by unique name.
     *
     * @param name The location name.
     * @return The registered location or null.ø
     */
    @Nullable
    public ITerminalStorageLocation<?> getLocation(ResourceLocation name);

    /**
     * @return All registered locations.
     */
    public Collection<ITerminalStorageLocation<?>> getLocations();

}
