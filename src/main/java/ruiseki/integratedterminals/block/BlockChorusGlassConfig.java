package ruiseki.integratedterminals.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import ruiseki.integratedterminals.IntegratedTerminals;
import ruiseki.integratedterminals.Reference;
import ruiseki.okcore.block.BlockGlassBase;
import ruiseki.okcore.config.extendedconfig.BlockConfig;

/**
 * Config for the Crystalized Menril block.
 * 
 * @author rubensworks
 *
 */
public class BlockChorusGlassConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockChorusGlassConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockChorusGlassConfig() {
        super(
            IntegratedTerminals._instance,
            true,
            "chorus_glass",
            null,
            config -> new BlockGlassBase(Material.glass, true) {

                @Override
                public int getRenderBlockPass() {
                    return 1;
                }

                @Override
                public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
                    // Allow any entity to walk through this block
                    return null;
                }
            });
    }

    @Override
    public String getOreDictionaryId() {
        return Reference.DICT_BLOCKGLASS;
    }
}
