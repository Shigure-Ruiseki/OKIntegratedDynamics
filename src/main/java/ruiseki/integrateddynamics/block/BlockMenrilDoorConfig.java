package ruiseki.integrateddynamics.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.block.BlockDoorBase;
import ruiseki.okcore.config.extendedconfig.BlockDoorConfig;

/**
 * Config for the Menril Door.
 *
 * @author josephcsible
 *
 */
public class BlockMenrilDoorConfig extends BlockDoorConfig {

    /**
     * The unique instance.
     */
    public static BlockMenrilDoorConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockMenrilDoorConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "menril_door",
            null,
            eConfig -> new BlockDoorBase(Material.wood).setStepSound(Block.soundTypeWood)
                .setHardness(3.0F));
    }
}
