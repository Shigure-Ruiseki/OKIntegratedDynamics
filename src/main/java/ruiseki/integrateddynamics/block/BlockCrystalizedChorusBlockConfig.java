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
public class BlockCrystalizedChorusBlockConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockCrystalizedChorusBlockConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockCrystalizedChorusBlockConfig() {
        super(IntegratedDynamics._instance, true, "crystalized_chorus_block", null, config -> createBlock());
    }

    public static Block createBlock() {
        Block block = new BlockBase(Material.clay);
        block.setHardness(1.5F);
        block.setStepSound(Block.soundTypeSnow);
        block.setHarvestLevel("pickaxe", 0);
        return block;
    }

}
