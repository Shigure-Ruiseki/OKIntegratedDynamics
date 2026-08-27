package ruiseki.integratedterminals.core.terminalstorage.location;

import java.util.Collection;
import java.util.Map;

import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;

import ruiseki.integratedterminals.api.terminalstorage.location.ITerminalStorageLocation;
import ruiseki.integratedterminals.api.terminalstorage.location.ITerminalStorageLocationRegistry;

/**
 * Implementation of {@link ITerminalStorageLocationRegistry}.
 * 
 * @author rubensworks
 */
public class TerminalStorageLocationRegistry implements ITerminalStorageLocationRegistry {

    private static TerminalStorageLocationRegistry INSTANCE = new TerminalStorageLocationRegistry();

    private final Map<String, ITerminalStorageLocation<?>> locations = Maps.newLinkedHashMap();

    /**
     * @return The unique instance.
     */
    public static TerminalStorageLocationRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public <L, T extends ITerminalStorageLocation<L>> T register(T location) {
        locations.put(
            location.getName()
                .toString(),
            location);
        return location;
    }

    @Nullable
    @Override
    public ITerminalStorageLocation<?> getLocation(ResourceLocation name) {
        return locations.get(name.toString());
    }

    @Override
    public Collection<ITerminalStorageLocation<?>> getLocations() {
        return locations.values();
    }
}
