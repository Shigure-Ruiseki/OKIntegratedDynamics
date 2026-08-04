package ruiseki.integrateddynamics.world.biome;

import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

import ruiseki.integrateddynamics.world.gen.WorldGeneratorMenrilTree;

/**
 * Decorator for the Meneglin biome.
 * 
 * @author rubensworks
 */
public class MeneglinBiomeDecorator extends BiomeDecorator {

    public static final WorldGeneratorMenrilTree MENRIL_TREE_GEN = new WorldGeneratorMenrilTree(false);

    @Override
    protected void genDecorations(BiomeGenBase biomeGenBaseIn) {
        super.genDecorations(biomeGenBaseIn);

        int k1 = this.treesPerChunk / 3;
        if (this.randomGenerator.nextInt(10) == 0) {
            ++k1;
        }

        if (TerrainGen
            .decorate(currentWorld, randomGenerator, chunk_X, chunk_Z, DecorateBiomeEvent.Decorate.EventType.TREE)) {
            for (int j2 = 0; j2 < k1; ++j2) {
                int x = this.chunk_X + this.randomGenerator.nextInt(16) + 8;
                int z = this.chunk_Z + this.randomGenerator.nextInt(16) + 8;
                int y = this.currentWorld.getHeightValue(x, z);

                MENRIL_TREE_GEN.growTree(this.currentWorld, this.randomGenerator, x, y, z);
            }
        }
    }
}
