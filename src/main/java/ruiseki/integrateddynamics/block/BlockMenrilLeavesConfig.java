package ruiseki.integrateddynamics.block;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.item.ItemMenrilBerriesConfig;
import ruiseki.okcore.config.ConfigurableProperty;
import ruiseki.okcore.config.ConfigurableTypeCategory;
import ruiseki.okcore.config.configurable.ConfigurableBlockLeaves;
import ruiseki.okcore.config.configurable.IConfigurable;
import ruiseki.okcore.config.extendedconfig.BlockConfig;

/**
 * Config for the Menril Leaves.
 *
 * @author rubensworks
 *
 */
public class BlockMenrilLeavesConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockMenrilLeavesConfig _instance;

    /**
     * A 1/x chance menril berries will be dropped when breaking a leaves block.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.BLOCK,
        comment = "A 1/x chance menril berries will be dropped when breaking a leaves block.",
        isCommandable = true,
        minimalValue = 0)
    public static int berriesDropChance = 4;

    /**
     * Make a new instance.
     */
    public BlockMenrilLeavesConfig() {
        super(IntegratedDynamics._instance, true, "menril_leaves", null, null);
    }

    @Override
    protected IConfigurable initSubInstance() {
        return (ConfigurableBlockLeaves) new ConfigurableBlockLeaves(this) {

            @Override
            public Item getItemDropped(int meta, Random random, int i1) {
                return Item.getItemFromBlock(BlockMenrilSaplingConfig._instance.getBlockInstance());
            }

            @Override
            public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
                ArrayList<ItemStack> drops = super.getDrops(world, x, y, z, metadata, fortune);
                if (!world.isRemote) {
                    if (world.rand.nextInt(berriesDropChance) == 0) {
                        drops.add(new ItemStack(ItemMenrilBerriesConfig._instance.getItemInstance()));
                    }
                }
                return drops;
            }

            @Override
            protected ItemStack createStackedBlock(int meta) {
                return new ItemStack(this);
            }
        }.setHardness(0.2F)
            .setLightLevel(0.65F)
            .setLightOpacity(1)
            .setStepSound(Block.soundTypeGrass);
    }

    @Override
    public String getOreDictionaryId() {
        return Reference.DICT_TREELEAVES;
    }

    @Override
    public void onRegistered() {
        Blocks.fire.setFireInfo(getBlockInstance(), 5, 20);
    }

}
