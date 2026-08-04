package ruiseki.integrateddynamics.block;

import net.minecraft.block.material.Material;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.world.gen.WorldGeneratorMenrilTree;
import ruiseki.okcore.config.configurable.ConfigurableBlockSapling;
import ruiseki.okcore.config.configurable.IConfigurable;
import ruiseki.okcore.config.extendedconfig.BlockConfig;

/**
 * Config for the Menril Sapling.
 * 
 * @author rubensworks
 *
 */
public class BlockMenrilSaplingConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockMenrilSaplingConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockMenrilSaplingConfig() {
        super(IntegratedDynamics._instance, true, "menrilSapling", null, null);
    }

    @Override
    protected IConfigurable initSubInstance() {
        return new ConfigurableBlockSapling(this, Material.plants, new WorldGeneratorMenrilTree(false));
    }

    @Override
    public String getOreDictionaryId() {
        return Reference.DICT_SAPLINGTREE;
    }

}
