package ruiseki.integrateddynamics.item;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.ItemConfig;
import ruiseki.okcore.item.ItemBase;

/**
 * Config for the Crystalized Menril Chunk.
 *
 * @author rubensworks
 *
 */
public class ItemCrystalizedMenrilChunkConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static ItemCrystalizedMenrilChunkConfig _instance;

    /**
     * Make a new instance.
     */
    public ItemCrystalizedMenrilChunkConfig() {
        super(IntegratedDynamics._instance, true, "crystalized_menril_chunk", null, config -> new ItemBase());
    }
}
