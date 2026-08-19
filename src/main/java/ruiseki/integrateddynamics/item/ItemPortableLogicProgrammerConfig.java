package ruiseki.integrateddynamics.item;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.ItemConfig;

/**
 * Config for the portable logic programmer.
 *
 * @author rubensworks
 */
public class ItemPortableLogicProgrammerConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static ItemPortableLogicProgrammerConfig _instance;

    /**
     * Make a new instance.
     */
    public ItemPortableLogicProgrammerConfig() {
        super(IntegratedDynamics._instance, true, "portable_logic_programmer", null, ItemPortableLogicProgrammer::new);
    }

}
