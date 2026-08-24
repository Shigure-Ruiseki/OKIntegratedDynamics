package ruiseki.integrateddynamics.block;

import java.util.Random;

import net.minecraft.world.World;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.okcore.block.BlockTorchBase;
import ruiseki.okcore.config.extendedconfig.BlockConfig;

/**
 * Config for the Menril Torch.
 * 
 * @author rubensworks
 *
 */
public class BlockMenrilTorchConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockMenrilTorchConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockMenrilTorchConfig() {
        super(IntegratedDynamics._instance, true, "menril_torch", null, config -> new BlockTorchBase() {

            @Override
            public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
                // No particles
            }
        });
    }

    @Override
    public String getOreDictionaryId() {
        return Reference.DICT_TORCH;
    }

}
