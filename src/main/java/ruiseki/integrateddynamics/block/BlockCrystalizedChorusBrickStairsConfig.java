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
public class BlockCrystalizedChorusBrickStairsConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockCrystalizedChorusBrickStairsConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockCrystalizedChorusBrickStairsConfig() {
        super(IntegratedDynamics._instance, true, "crystalized_chorus_brick_stairs", null, config -> createBlock());
    }

    public static BlockStairsBase createBlock() {
        BlockStairsBase block = new BlockStairsBase(BlockCrystalizedChorusBrickConfig._instance.getInstance());
        block.setStepSound(Block.soundTypeSnow);
        block.setHardness(1.5F);
        block.setHarvestLevel("pickaxe", 0);
        return block;
    }

}
