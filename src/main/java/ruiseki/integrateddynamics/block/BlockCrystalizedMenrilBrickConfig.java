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
public class BlockCrystalizedMenrilBrickConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockCrystalizedMenrilBrickConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockCrystalizedMenrilBrickConfig() {
        super(IntegratedDynamics._instance, true, "crystalized_menril_brick", null, config -> createBlock());
    }

    public static Block createBlock() {
        Block block = new BlockBase(Material.clay);
        block.setStepSound(Block.soundTypeSnow);
        block.setHardness(1.5F);
        block.setHarvestLevel("pickaxe", 0);
        return block;
    }

}
