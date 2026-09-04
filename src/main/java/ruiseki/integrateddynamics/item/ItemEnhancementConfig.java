package ruiseki.integrateddynamics.item;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.ItemConfig;

public class ItemEnhancementConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static ItemEnhancementConfig _instance;

    /**
     * Make a new instance.
     */
    public ItemEnhancementConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "enhancement_offset",
            null,
            config -> new ItemEnhancement(ItemEnhancement.Type.OFFSET));
    }
}
