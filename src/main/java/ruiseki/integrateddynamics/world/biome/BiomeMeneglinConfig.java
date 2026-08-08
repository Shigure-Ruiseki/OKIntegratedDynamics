package ruiseki.integrateddynamics.world.biome;

import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.okcore.config.ConfigurableProperty;
import ruiseki.okcore.config.ConfigurableTypeCategory;
import ruiseki.okcore.config.extendedconfig.BiomeConfig;

/**
 * Config for {@link BiomeMeneglin}.
 *
 * @author rubensworks
 *
 */
public class BiomeMeneglinConfig extends BiomeConfig {

    /**
     * The unique instance.
     */
    public static BiomeMeneglinConfig _instance;

    /**
     * The weight of spawning.
     */
    @ConfigurableProperty(category = ConfigurableTypeCategory.BIOME, comment = "The weight of spawning.")
    public static int spawnWeight = 5;

    /**
     * Make a new instance.
     */
    public BiomeMeneglinConfig() {
        super(IntegratedDynamics._instance, Reference.BIOME_MENEGLIN, "biomeMeneglin", null, BiomeMeneglin.class);
    }

    @Override
    public void registerBiomeDictionary() {
        if (spawnWeight > 0) {
            BiomeManager.addBiome(BiomeManager.BiomeType.COOL, new BiomeManager.BiomeEntry(getBiome(), spawnWeight));
        }
        BiomeManager.addSpawnBiome(getBiome());
        BiomeManager.addStrongholdBiome(getBiome());
        BiomeManager.addVillageBiome(getBiome(), true);
        BiomeDictionary.registerBiomeType(getBiome(), BiomeDictionary.Type.COLD, BiomeDictionary.Type.MAGICAL);
    }

}
