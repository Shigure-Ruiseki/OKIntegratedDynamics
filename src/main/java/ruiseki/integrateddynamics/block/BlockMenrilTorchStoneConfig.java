package ruiseki.integrateddynamics.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.okcore.block.BlockTorchBase;
import ruiseki.okcore.config.extendedconfig.BlockConfig;

/**
 * Config for the Menril Stone Torch.
 * 
 * @author rubensworks
 *
 */
public class BlockMenrilTorchStoneConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockMenrilTorchStoneConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockMenrilTorchStoneConfig() {
        super(IntegratedDynamics._instance, true, "menril_torch_stone", null, config -> new BlockTorchBase() {

            @Override
            public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
                // No particles
            }
        }.setStepSound(Block.soundTypeStone));
    }

    @Override
    public String getOreDictionaryId() {
        return Reference.DICT_TORCH;
    }
}
