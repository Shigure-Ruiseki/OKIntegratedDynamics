package ruiseki.integrateddynamics.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.okcore.block.BlockBase;
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
        super(
            IntegratedDynamics._instance,
            true,
            "menril_planks",
            null,
            config -> new BlockBase(Material.wood).setHardness(2.0F)
                .setStepSound(Block.soundTypeWood));
    }

    @Override
    public String getOreDictionaryId() {
        return Reference.DICT_WOODPLANK;
    }

    @Override
    public void onRegistered() {
        Blocks.fire.setFireInfo(getInstance(), 5, 20);
    }

}
