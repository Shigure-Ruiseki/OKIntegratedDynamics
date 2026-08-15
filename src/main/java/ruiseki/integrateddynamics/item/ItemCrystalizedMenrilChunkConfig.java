package ruiseki.integrateddynamics.item;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.configurable.ConfigurableItem;
import ruiseki.okcore.config.extendedconfig.ItemConfig;

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
        super(IntegratedDynamics._instance, true, "crystalized_menril_chunk", null, null);
    }

    @Override
    protected ConfigurableItem initSubInstance() {
        return new ConfigurableItem(this);
    }

}
