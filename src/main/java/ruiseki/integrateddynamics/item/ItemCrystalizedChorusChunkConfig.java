package ruiseki.integrateddynamics.item;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.ItemConfig;
import ruiseki.okcore.item.ItemBase;

/**
 * Config for the Crystalized Chorus Chunk.
 * 
 * @author rubensworks
 *
 */
public class ItemCrystalizedChorusChunkConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static ItemCrystalizedChorusChunkConfig _instance;

    public ItemCrystalizedChorusChunkConfig() {
        super(IntegratedDynamics._instance, true, "crystalized_chorus_chunk", null, eConfig -> new ItemBase());
    }

}
