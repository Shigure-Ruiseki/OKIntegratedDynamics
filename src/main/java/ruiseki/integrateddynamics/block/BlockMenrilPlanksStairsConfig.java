package ruiseki.integrateddynamics.block;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.okcore.block.BlockStairsBase;
import ruiseki.okcore.config.extendedconfig.BlockConfig;

/**
 * Config for the Menril Wood Stairs.
 *
 * @author rubensworks
 *
 */
public class BlockMenrilPlanksStairsConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockMenrilPlanksStairsConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockMenrilPlanksStairsConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "menril_planks_stairs",
            null,
            config -> new BlockStairsBase(BlockMenrilPlanksConfig._instance.getInstance())
                .setStepSound(Block.soundTypeWood)
                .setHardness(2.0F));
    }

    @Override
    public String getOreDictionaryId() {
        return Reference.DICT_STAIRWOOD;
    }

    @Override
    public void onRegistered() {
        Blocks.fire.setFireInfo(getInstance(), 5, 20);
    }

}
