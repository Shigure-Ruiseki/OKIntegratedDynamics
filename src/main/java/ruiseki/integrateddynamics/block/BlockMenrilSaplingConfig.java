package ruiseki.integrateddynamics.block;

import net.minecraft.block.material.Material;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.world.biome.MeneglinBiomeDecorator;
import ruiseki.integrateddynamics.world.gen.WorldGeneratorMenrilTree;
import ruiseki.okcore.config.configurable.ConfigurableBlockSapling;
import ruiseki.okcore.config.configurable.IConfigurable;
import ruiseki.okcore.config.extendedconfig.BlockConfig;

/**
 * Config for the Menril Sapling.
 *
 * @author rubensworks
 *
 */
public class BlockMenrilSaplingConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockMenrilSaplingConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockMenrilSaplingConfig() {
        super(IntegratedDynamics._instance, true, "menrilSapling", null, null);
    }

    @Override
    protected IConfigurable initSubInstance() {
        return new ConfigurableBlockSapling(this, Material.plants, new WorldGeneratorMenrilTree(false));
    }

    @Override
    public String getOreDictionaryId() {
        return Reference.DICT_SAPLINGTREE;
    }

    @SubscribeEvent
    public void onDecorate(DecorateBiomeEvent.Decorate decorateBiomeEvent) {
        if (decorateBiomeEvent.type == DecorateBiomeEvent.Decorate.EventType.TREE) {
            if (GeneralConfig.wildMenrilTreeChance > 0
                && decorateBiomeEvent.rand.nextInt(GeneralConfig.wildMenrilTreeChance) == 0) {
                int x = decorateBiomeEvent.chunkX + decorateBiomeEvent.rand.nextInt(16) + 8;
                int z = decorateBiomeEvent.chunkZ + decorateBiomeEvent.rand.nextInt(16) + 8;
                int y = decorateBiomeEvent.world.getHeightValue(x, z);

                MeneglinBiomeDecorator.MENRIL_TREE_GEN
                    .growTree(decorateBiomeEvent.world, decorateBiomeEvent.rand, x, y, z);
            }
        }
    }
}
