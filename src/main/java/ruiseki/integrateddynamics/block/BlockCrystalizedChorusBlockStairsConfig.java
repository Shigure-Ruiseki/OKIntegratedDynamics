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
public class BlockCrystalizedChorusBlockStairsConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockCrystalizedChorusBlockStairsConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockCrystalizedChorusBlockStairsConfig() {
        super(IntegratedDynamics._instance, true, "crystalized_chorus_block_stairs", null, config -> createBlock());
    }

    public static BlockStairsBase createBlock() {
        BlockStairsBase block = new BlockStairsBase(BlockCrystalizedChorusBlockConfig._instance.getInstance());
        block.setStepSound(Block.soundTypeSnow);
        block.setHardness(1.5F);
        block.setHarvestLevel("pickaxe", 0);
        return block;
    }

}
