package ruiseki.integrateddynamics.block;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.okcore.config.configurable.ConfigurableBlockLog;
import ruiseki.okcore.config.configurable.IConfigurable;
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
        super(IntegratedDynamics._instance, true, "menrilLog", null, null);
    }

    @Override
    protected IConfigurable initSubInstance() {
        return (ConfigurableBlockLog) new ConfigurableBlockLog(this).setHardness(2.0F)
            .setStepSound(Block.soundTypeWood);
    }

    @Override
    public String getOreDictionaryId() {
        return Reference.DICT_WOODLOG;
    }

    @Override
    public void onRegistered() {
        Blocks.fire.setFireInfo(getBlockInstance(), 5, 20);
    }

}
