package ruiseki.integrateddynamics.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.block.BlockBase;
import ruiseki.okcore.config.extendedconfig.BlockConfig;

/**
 * Config for the Crystalized Menril block.
 * 
 * @author rubensworks
 *
 */
public class BlockCrystalizedMenrilBlockConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockCrystalizedMenrilBlockConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockCrystalizedMenrilBlockConfig() {
        super(IntegratedDynamics._instance, true, "crystalized_menril_block", null, eConfig -> createBlock());
    }

    public static Block createBlock() {
        Block block = new BlockBase(Material.clay);
        block.setStepSound(Block.soundTypeSnow);
        block.setHardness(1.5f);
        block.setHarvestLevel("pickaxe", 0);
        return block;
    }

}
