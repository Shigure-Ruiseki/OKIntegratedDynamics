package ruiseki.integrateddynamics.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.block.BlockBase;
import ruiseki.okcore.config.extendedconfig.BlockConfig;

/**
 * Config for the Crystalized Chorus block.
 * 
 * @author rubensworks
 *
 */
public class BlockCrystalizedChorusBrickConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockCrystalizedChorusBrickConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockCrystalizedChorusBrickConfig() {
        super(IntegratedDynamics._instance, true, "crystalized_chorus_brick", null, config -> createBlock());
    }

    public static Block createBlock() {
        Block block = new BlockBase(Material.clay);
        block.setHardness(1.5F);
        block.setStepSound(Block.soundTypeSnow);
        block.setHarvestLevel("pickaxe", 0);
        return block;
    }

}
