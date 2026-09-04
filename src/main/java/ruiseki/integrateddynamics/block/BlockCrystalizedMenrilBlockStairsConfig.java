package ruiseki.integrateddynamics.block;

import net.minecraft.block.Block;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.block.BlockStairsBase;
import ruiseki.okcore.config.extendedconfig.BlockConfig;

/**
 * Config for the Crystallized Menril Block Stairs.
 * 
 * @author rubensworks
 *
 */
public class BlockCrystalizedMenrilBlockStairsConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockCrystalizedMenrilBlockStairsConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockCrystalizedMenrilBlockStairsConfig() {
        super(IntegratedDynamics._instance, true, "crystalized_menril_block_stairs", null, config -> createBlock());
    }

    public static BlockStairsBase createBlock() {
        BlockStairsBase block = new BlockStairsBase(BlockCrystalizedMenrilBlockConfig._instance.getInstance());
        block.setStepSound(Block.soundTypeSnow);
        block.setHardness(1.5F);
        block.setHarvestLevel("pickaxe", 0);
        return block;
    }

}
