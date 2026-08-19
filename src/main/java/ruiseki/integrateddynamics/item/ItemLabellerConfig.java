package ruiseki.integrateddynamics.item;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.ItemConfig;

/**
 * Config for the labeller.
 *
 * @author rubensworks
 */
public class ItemLabellerConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static ItemLabellerConfig _instance;

    /**
     * Make a new instance.
     */
    public ItemLabellerConfig() {
        super(IntegratedDynamics._instance, true, "labeller", null, ItemLabeller::new);
    }

}
