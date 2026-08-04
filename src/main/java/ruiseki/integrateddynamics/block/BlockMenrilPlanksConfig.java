package ruiseki.integrateddynamics.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.okcore.config.configurable.ConfigurableBlock;
import ruiseki.okcore.config.configurable.IConfigurable;
import ruiseki.okcore.config.extendedconfig.BlockConfig;

/**
 * Config for the Menril Planks.
 * 
 * @author rubensworks
 *
 */
public class BlockMenrilPlanksConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockMenrilPlanksConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockMenrilPlanksConfig() {
        super(IntegratedDynamics._instance, true, "menrilPlanks", null, null);
    }

    @Override
    protected IConfigurable initSubInstance() {
        return (ConfigurableBlock) new ConfigurableBlock(this, Material.wood).setHardness(2.0F)
            .setStepSound(Block.soundTypeWood);
    }

    @Override
    public String getOreDictionaryId() {
        return Reference.DICT_WOODPLANK;
    }

    @Override
    public void onRegistered() {
        Blocks.fire.setFireInfo(getBlockInstance(), 5, 20);
    }

}
