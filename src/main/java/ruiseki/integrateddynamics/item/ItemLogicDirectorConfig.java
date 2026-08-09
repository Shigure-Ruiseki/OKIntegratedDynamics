package ruiseki.integrateddynamics.item;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.configurable.ConfigurableItem;
import ruiseki.okcore.config.configurable.IConfigurable;
import ruiseki.okcore.config.extendedconfig.ItemConfig;

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
        super(IntegratedDynamics._instance, true, "logic_director", null, null);
    }

    @Override
    protected IConfigurable initSubInstance() {
        return new ConfigurableItem(this);
    }

}
