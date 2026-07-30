package ruiseki.integrateddynamics.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.IntegerProperty;
import ruiseki.okcore.config.configurable.ConfigurableBlock;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.helper.BlockStateHelpers;

/**
 * An invisible light source with variable intensity.
 *
 * @author rubensworks
 */
public class BlockInvisibleLight extends ConfigurableBlock {

    @BlockProperty
    public static final IntegerProperty LIGHT = IntegerProperty.construct(
        "light",
        0,
        IBlockAccess::getBlockMetadata,
        (world, x, y, z, value) -> world.setBlockMetadataWithNotify(x, y, z, value, 3));

    private static BlockInvisibleLight _instance = null;

    /**
     * Get the unique instance.
     *
     * @return The instance.
     */
    public static BlockInvisibleLight getInstance() {
        return _instance;
    }

    /**
     * Make a new block instance.
     *
     * @param eConfig Config for this block.
     */
    public BlockInvisibleLight(ExtendedConfig eConfig) {
        super(eConfig, Material.air);

        setHardness(3.0F);
        setStepSound(soundTypeMetal);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean canCollideCheck(int metadata, boolean hitIfLiquid) {
        return false;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public Item getItemDropped(int metadata, Random random, int fortune) {
        return null;
    }

    @Override
    public int getMobilityFlag() {
        return 0;
    }

    @Override
    public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        // Do not appear in creative tab
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        Integer value = BlockStateHelpers.get(world, x, y, z, LIGHT);
        return value != null ? value : 0;
    }

    @Override
    public int getLightValue() {
        return 15; // Required for light update when this block is removed
    }
}
