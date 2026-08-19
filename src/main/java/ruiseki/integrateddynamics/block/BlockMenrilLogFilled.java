package ruiseki.integrateddynamics.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ruiseki.integrateddynamics.item.ItemCrystalizedMenrilChunkConfig;
import ruiseki.okcore.block.BlockLogBase;

/**
 * Menril wood block that is filled.
 *
 * @author rubensworks
 */
public class BlockMenrilLogFilled extends BlockLogBase {

    private static BlockMenrilLogFilled _instance = null;

    /**
     * Get the unique instance.
     *
     * @return The instance.
     */
    public static BlockMenrilLogFilled getInstance() {
        return _instance;
    }

    /**
     * Make a new block instance.
     */
    public BlockMenrilLogFilled() {
        super();
        this.setHardness(2.5F);
        this.setStepSound(soundTypeWood);
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Item.getItemFromBlock(BlockMenrilLog.getInstance());
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        drops.add(new ItemStack(getItemDropped(metadata, world.rand, fortune)));
        drops.add(
            new ItemStack(
                ItemCrystalizedMenrilChunkConfig._instance.getInstance(),
                1 + world.rand.nextInt(3 + fortune)));
        return drops;
    }

    @Override
    protected boolean canSilkHarvest() {
        return true;
    }
}
