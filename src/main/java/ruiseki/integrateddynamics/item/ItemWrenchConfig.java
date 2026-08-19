package ruiseki.integrateddynamics.item;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.ItemConfig;

/**
 * Config for a wrench.
 *
 * @author rubensworks
 */
public class ItemWrenchConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static ItemWrenchConfig _instance;

    /**
     * Make a new instance.
     */
    public ItemWrenchConfig() {
        super(IntegratedDynamics._instance, true, "wrench", null, config -> new ItemWrench());
    }

}
