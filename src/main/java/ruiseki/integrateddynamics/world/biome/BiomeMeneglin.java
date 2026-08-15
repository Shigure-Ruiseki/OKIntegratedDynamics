package ruiseki.integrateddynamics.world.biome;

import java.util.Random;

import net.minecraft.block.BlockFlower;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;

import org.apache.commons.lang3.ArrayUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.config.configurable.ConfigurableBiome;
import ruiseki.okcore.config.extendedconfig.BiomeConfig;
import ruiseki.okcore.helper.Helpers;

/**
 * Meneglin biome.
 *
 * @author rubensworks
 *
 */
public class BiomeMeneglin extends ConfigurableBiome {

    private static final int[] FLOWER_METAS = new int[] { 1, 8, 6 };

    private static BiomeGenBase _instance = null;

    public static BiomeGenBase getInstance() {
        return _instance;
    }

    public BiomeMeneglin(BiomeConfig eConfig) {
        super(eConfig);

        this.setHeight(new BiomeGenBase.Height(0.4F, 0.4F));
        this.setTemperatureRainfall(0.75F, 0.25F);
        this.setColor(Helpers.RGBToInt(178, 226, 222));
        this.waterColorMultiplier = Helpers.RGBToInt(85, 221, 168);

        this.theBiomeDecorator.treesPerChunk = 3;
        this.theBiomeDecorator.flowersPerChunk = 70;

        MinecraftForge.TERRAIN_GEN_BUS.register(this);
    }

    @Override
    public void decorate(World world, Random random, int chunkX, int chunkZ) {
        if (!ArrayUtils.contains(BiomeMeneglinConfig.meneglinBiomeDimensionBlacklist, world.provider.dimensionId)) {
            super.decorate(world, random, chunkX, chunkZ);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getBiomeGrassColor(int x, int y, int z) {
        return Helpers.RGBToInt(85, 221, 168);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getBiomeFoliageColor(int x, int y, int z) {
        return Helpers.RGBToInt(128, 208, 185);
    }

    @Override
    public int getSkyColorByTemp(float temp) {
        return Helpers.RGBToInt(178, 238, 233);
    }

    @Override
    public BiomeDecorator createBiomeDecorator() {
        return getModdedBiomeDecorator(new MeneglinBiomeDecorator());
    }

    @Override
    public String func_150572_a(Random rand, int x, int y, int z) {
        int meta = FLOWER_METAS[rand.nextInt(FLOWER_METAS.length)];
        return BlockFlower.field_149859_a[meta];
    }

    @Override
    public void addDefaultFlowers() {
        for (int meta : FLOWER_METAS) {
            this.addFlower(Blocks.red_flower, meta, 20);
        }
    }

}
