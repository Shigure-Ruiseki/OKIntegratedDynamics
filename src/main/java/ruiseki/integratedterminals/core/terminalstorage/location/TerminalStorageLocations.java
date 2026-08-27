package ruiseki.integratedterminals.core.terminalstorage.location;

import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.api.terminalstorage.location.ITerminalStorageLocationRegistry;

/**
 * @author rubensworks
 */
public class TerminalStorageLocations {

    public static ITerminalStorageLocationRegistry REGISTRY = IntegratedTerminals._instance.getRegistryManager()
        .getRegistry(ITerminalStorageLocationRegistry.class);

    public static TerminalStorageLocationPart PART;
    public static TerminalStorageLocationItem ITEM;

    public static void load() {
        PART = TerminalStorageLocations.REGISTRY.register(new TerminalStorageLocationPart());
        ITEM = TerminalStorageLocations.REGISTRY.register(new TerminalStorageLocationItem());
    }

}
