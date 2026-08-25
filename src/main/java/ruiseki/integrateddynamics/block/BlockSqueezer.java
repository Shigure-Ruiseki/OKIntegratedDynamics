package ruiseki.integrateddynamics.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;
import com.gtnewhorizon.gtnhlib.geometry.Axis;

import ruiseki.integrateddynamics.tileentity.TileSqueezer;
import ruiseki.okcore.block.BlockTile;
import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.IEnumProperty;
import ruiseki.okcore.block.property.IntegerProperty;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.BlockStateHelpers;
import ruiseki.okcore.helper.DirectionHelpers;
import ruiseki.okcore.helper.TileHelpers;

/**
 * A block for squeezing stuff.
 *
 * @author rubensworks
 */
public class BlockSqueezer extends BlockTile {

    @BlockProperty
    public static final IEnumProperty<EnumAxis> AXIS = IEnumProperty
        .construct("axis", EnumAxis.class, EnumAxis.X, (world, x, y, z) -> {
            TileSqueezer tile = TileHelpers.getSafeTile(world, x, y, z, TileSqueezer.class);
            return tile != null ? tile.getAxis() : EnumAxis.X;
        }, (world, x, y, z, value) -> {
            TileSqueezer tile = TileHelpers.getSafeTile(world, x, y, z, TileSqueezer.class);
            if (tile != null) tile.setAxis(value);
        });
    @BlockProperty
    public static final IntegerProperty HEIGHT = IntegerProperty.construct("height", 1, (world, x, y, z) -> {
        TileSqueezer tile = TileHelpers.getSafeTile(world, x, y, z, TileSqueezer.class);
        return tile != null ? tile.getHeight() : 1;
    }, (world, x, y, z, value) -> {
        TileSqueezer tile = TileHelpers.getSafeTile(world, x, y, z, TileSqueezer.class);
        if (tile != null) tile.setHeight(value);
    }); // 1 is heighest, 7 is lowest

    /**
     * Make a new block instance.
     */
    public BlockSqueezer() {
        super(Material.iron, TileSqueezer.class);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        if (world.isRemote) {
            return true;
        } else if (BlockStateHelpers.get(world, x, y, z, HEIGHT) == 1) {
            TileSqueezer tile = TileHelpers.getSafeTile(world, x, y, z, TileSqueezer.class);
            if (tile != null) {
                ItemStack itemStack = player.inventory.getCurrentItem();
                ItemStack tileStack = tile.getStackInSlot(0);

                if (itemStack == null && tileStack != null) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, tileStack);
                    tile.setInventorySlotContents(0, null);
                    tile.sendUpdate();
                    return true;
                } else if (player.inventory.addItemStackToInventory(tileStack)) {
                    tile.setInventorySlotContents(0, null);
                    tile.sendUpdate();
                    return true;
                } else if (itemStack != null && tile.getStackInSlot(0) == null) {
                    tile.setInventorySlotContents(0, itemStack.splitStack(1));
                    if (itemStack.stackSize <= 0)
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    tile.sendUpdate();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onFallenUpon(World world, int x, int y, int z, Entity entity, float fallDistance) {
        super.onFallenUpon(world, x, y, z, entity, fallDistance);

        if (!world.isRemote && fallDistance >= 0.5F && entity instanceof EntityLivingBase) {
            BlockPos blockPos = new BlockPos(x, y, z);
            BlockState blockState = BlockStateHelpers.getState(world, blockPos);

            int steps = 1 + MathHelper.floor_float((fallDistance - 0.5F));

            if (blockState.getBlock() == this) {
                int newHeight = Math.min(7, blockState.getPropertyValue(HEIGHT) + steps);
                blockState.setPropertyValue(HEIGHT, newHeight);
                blockState.place(world, x, y, z, 3);
                TileSqueezer tile = TileHelpers.getSafeTile(world, blockPos, TileSqueezer.class);
                if (tile != null) {
                    tile.setItemHeight(Math.max(newHeight, tile.getItemHeight()));
                }
            }
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighborBlock) {
        super.onNeighborBlockChange(world, x, y, z, neighborBlock);
        if (!world.isRemote) {
            if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
                BlockStateHelpers.set(world, x, y, z, HEIGHT, 1);
                AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1);
                List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, box);
                for (Entity entity : entities) {
                    entity.motionY += 0.25D;
                    entity.posY += 0.5D;
                }
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(World world, BlockPos pos, ForgeDirection facing, float hitX, float hitY,
        float hitZ, int meta, EntityLivingBase placer) {
        BlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);;
        Axis axis = Axis.fromDirection(DirectionHelpers.yawToDirection4(placer));
        state.setPropertyValue(AXIS, EnumAxis.fromFacingAxis(axis));
        return state;
    }

    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List list,
        Entity entity) {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);

        float f = 0.125F;

        this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, f);
        super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);

        this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, f);
        super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);

        this.setBlockBounds(0.0F, 0.0F, 1.0F - f, f, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);

        this.setBlockBounds(1.0F - f, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);

        int height = BlockStateHelpers.get(world, x, y, z, HEIGHT);

        float maxY = (8 - height + 1) * 0.125F;
        float minY = maxY - 0.125F;

        this.setBlockBounds(0.0F, minY, 0.0F, 1.0F, maxY, 1.0F);
        super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);

        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int side) {
        return (int) (((double) BlockStateHelpers.get(world, x, y, z, HEIGHT) - 1) / 6.0D * 15.0D);
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return side == ForgeDirection.DOWN;
    }

    public static enum EnumAxis {

        X("x", new ForgeDirection[] { ForgeDirection.EAST, ForgeDirection.WEST }),
        Z("z", new ForgeDirection[] { ForgeDirection.NORTH, ForgeDirection.SOUTH });

        private final String name;
        private final ForgeDirection[] sides;

        private EnumAxis(String name, ForgeDirection[] sides) {
            this.name = name;
            this.sides = sides;
        }

        public String toString() {
            return this.name;
        }

        public static BlockSqueezer.EnumAxis fromFacingAxis(Axis axis) {
            switch (axis) {
                case X:
                    return Z;
                case Z:
                    return X;
                default:
                    return X;
            }
        }

        public static EnumAxis getOrientation(int id) {
            EnumAxis[] values = values();
            if (id < 0 || id >= values.length) {
                return X;
            }
            return values[id];
        }

        public String getName() {
            return this.name;
        }

        public ForgeDirection[] getSides() {
            return sides;
        }
    }
}
