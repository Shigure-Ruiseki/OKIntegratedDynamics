package ruiseki.integratedterminals.item;

import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.okcore.config.extendedconfig.ItemConfig;

/**
 * Config for the portable logic programmer.
 *
 * @author rubensworks
 */
public class ItemTerminalStoragePortableConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static ItemTerminalStoragePortableConfig _instance;

    /**
     * Make a new instance.
     */
    public ItemTerminalStoragePortableConfig() {
        super(IntegratedTerminals._instance, true, "terminal_storage_portable", null, ItemTerminalStoragePortable::new);
    }

}
