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
public class ItemProtoChorusConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static ItemProtoChorusConfig _instance;

    /**
     * Make a new instance.
     */
    public ItemProtoChorusConfig() {
        super(IntegratedDynamics._instance, true, "proto_chorus", null, eConfig -> new ItemBase());
    }

}
