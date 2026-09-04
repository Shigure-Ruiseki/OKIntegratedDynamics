package ruiseki.integrateddynamics.block;

import net.minecraft.init.Blocks;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.okcore.config.extendedconfig.BlockConfig;

/**
 * Config for the Menril Log.
 *
 * @author rubensworks
 *
 */
public class BlockMenrilLogConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockMenrilLogConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockMenrilLogConfig() {
        super(IntegratedDynamics._instance, true, "menril_log", null, config -> new BlockMenrilLog());
    }

    @Override
    public String getOreDictionaryId() {
        return Reference.DICT_WOODLOG;
    }

    @Override
    public void onRegistered() {
        Blocks.fire.setFireInfo(getInstance(), 5, 20);
    }
}
