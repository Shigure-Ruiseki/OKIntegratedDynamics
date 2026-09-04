package ruiseki.integrateddynamics.item;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.ItemConfig;
import ruiseki.okcore.item.ItemBase;

/**
 * Config for the Logic Director.
 *
 * @author rubensworks
 *
 */
public class ItemLogicDirectorConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static ItemLogicDirectorConfig _instance;

    /**
     * Make a new instance.
     */
    public ItemLogicDirectorConfig() {
        super(IntegratedDynamics._instance, true, "logic_director", null, config -> new ItemBase());
    }
}
