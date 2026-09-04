package ruiseki.integratedterminals.block;

import net.minecraft.block.material.Material;

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
public class BlockMenrilGlassConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockMenrilGlassConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockMenrilGlassConfig() {
        super(
            IntegratedTerminals._instance,
            true,
            "menril_glass",
            null,
            config -> new BlockGlassBase(Material.glass, true){

                @Override
                public int getRenderBlockPass() {
                    return 1;
                }

            }.setLightLevel(1F));
    }

    @Override
    public String getOreDictionaryId() {
        return Reference.DICT_BLOCKGLASS;
    }
}
