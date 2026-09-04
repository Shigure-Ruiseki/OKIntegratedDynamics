package ruiseki.integratedtunnels.core;

import java.util.Iterator;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Iterators;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.ItemHelpers;

/**
 * An item storage for exporting item entities to the world.
 *
 * @author rubensworks
 */
public class ItemHandlerWorldEntityExportWrapper
    implements IIngredientComponentStorage<ItemStack, Integer>, IBlockSource {

    private final WorldServer world;
    private final BlockPos pos;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final int lifespan;
    private final int delayBeforePickup;
    private final ForgeDirection facing;
    private final double velocity;
    private final float yawOffset;
    private final float pitchOffset;
    private final boolean dispense;

    private final IIngredientComponentStorage<ItemStack, Integer> dispenseResultHandler;

    private static final BehaviorDefaultDispenseItem DISPENSE_ITEM_DIRECTLY = new BehaviorDefaultDispenseItem();

    public ItemHandlerWorldEntityExportWrapper(WorldServer world, BlockPos pos, double offsetX, double offsetY,
        double offsetZ, int lifespan, int delayBeforePickup, ForgeDirection facing, double velocity, double yawOffset,
        double pitchOffset, boolean dispense, IIngredientComponentStorage<ItemStack, Integer> dispenseResultHandler) {
        this.world = world;
        this.pos = pos;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.lifespan = lifespan;
        this.delayBeforePickup = delayBeforePickup;
        this.facing = facing;
        this.velocity = velocity;
        this.yawOffset = (float) yawOffset;
        this.pitchOffset = (float) pitchOffset;
        this.dispense = dispense;
        this.dispenseResultHandler = dispenseResultHandler;
    }

    protected void setThrowableHeading(EntityItem entity, double x, double y, double z, double velocity) {
        float f = MathHelper.sqrt_double(x * x + y * y + z * z);
        x = x / (double) f;
        y = y / (double) f;
        z = z / (double) f;
        x = x * velocity;
        y = y * velocity;
        z = z * velocity;
        entity.motionX = x;
        entity.motionY = y;
        entity.motionZ = z;
        float f1 = MathHelper.sqrt_double(x * x + z * z);
        entity.rotationYaw = (float) (Math.atan2(x, z) * (180D / Math.PI));
        entity.rotationPitch = (float) (Math.atan2(y, (double) f1) * (180D / Math.PI));
        entity.prevRotationYaw = entity.rotationYaw;
        entity.prevRotationPitch = entity.rotationPitch;
    }

    protected static void handleDispenseResult(IIngredientComponentStorage<ItemStack, Integer> dispenseResultHandler,
        IBlockSource blockSource, ItemStack itemStack) {
        ItemStack remaining = dispenseResultHandler.insert(itemStack, false);
        if (!ItemHelpers.isEmpty(remaining)) {
            DISPENSE_ITEM_DIRECTLY.dispense(blockSource, remaining);
        }
    }

    @Override
    public double getX() {
        return getXInt() + offsetX;
    }

    @Override
    public double getY() {
        return getYInt() + offsetY;
    }

    @Override
    public double getZ() {
        return getZInt() + offsetZ;
    }

    @Override
    public int getXInt() {
        return getBlockPos().getX();
    }

    @Override
    public int getYInt() {
        return getBlockPos().getY();
    }

    @Override
    public int getZInt() {
        return getBlockPos().getZ();
    }

    @Override
    public int getBlockMetadata() {
        return this.facing.ordinal();
    }

    public BlockPos getBlockPos() {
        return this.pos.offset(this.facing.getOpposite());
    }

    @Override
    public TileEntityDispenser getBlockTileEntity() {
        return new SimulatedTileEntityDispenser(dispenseResultHandler, this);
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public IngredientComponent<ItemStack, Integer> getComponent() {
        return IngredientComponent.ITEMSTACK;
    }

    @Override
    public Iterator<ItemStack> iterator() {
        return Iterators.emptyIterator();
    }

    @Override
    public Iterator<ItemStack> iterator(@Nonnull ItemStack prototype, Integer matchCondition) {
        return iterator();
    }

    @Override
    public long getMaxQuantity() {
        return 64;
    }

    private static float getHorizontalAngle(ForgeDirection direction) {
        switch (direction) {
            case NORTH:
                return 180.0F;
            case SOUTH:
                return 0.0F;
            case WEST:
                return 90.0F;
            case EAST:
                return 270.0F;
            default:
                return 0.0F;
        }
    }

    @Override
    public ItemStack insert(@Nonnull ItemStack stack, boolean simulate) {
        if (ItemHelpers.isEmpty(stack)) {
            return ItemHelpers.EMPTY;
        }

        if (!simulate) {
            if (this.dispense) {
                IBehaviorDispenseItem behaviorDispenseItem = (IBehaviorDispenseItem) BlockDispenser.dispenseBehaviorRegistry
                    .getObject(stack.getItem());
                if (behaviorDispenseItem != null
                    && behaviorDispenseItem.getClass() != BehaviorDefaultDispenseItem.class) {
                    ItemStack result = behaviorDispenseItem.dispense(this, ItemHelpers.copy(stack));
                    if (!ItemHelpers.isEmpty(result)) {
                        handleDispenseResult(this.dispenseResultHandler, this, result);
                    }
                    return ItemHelpers.EMPTY;
                }
            }

            EntityItem entity = new EntityItem(
                world,
                pos.getX() + offsetX,
                pos.getY() + offsetY,
                pos.getZ() + offsetZ,
                ItemHelpers.copy(stack));
            entity.lifespan = lifespan <= 0 ? stack.getItem()
                .getEntityLifespan(stack, world) : lifespan;

            float yaw = getHorizontalAngle(facing) + yawOffset;
            float pitch = (facing == ForgeDirection.UP ? -90F : (facing == ForgeDirection.DOWN ? 90F : 0))
                - pitchOffset;

            this.setThrowableHeading(
                entity,
                -MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F),
                -MathHelper.sin(pitch * 0.017453292F),
                MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F),
                this.velocity);

            entity.delayBeforeCanPickup = delayBeforePickup;
            world.spawnEntityInWorld(entity);
        } else if (this.dispense) {
            ItemStack result = ItemHelpers.copy(stack);
            ItemHelpers.shrink(result, 1);
            return ItemHelpers.isEmpty(result) ? ItemHelpers.EMPTY : result;
        }
        return ItemHelpers.EMPTY;
    }

    @Override
    public ItemStack extract(@Nonnull ItemStack prototype, Integer matchCondition, boolean simulate) {
        return ItemHelpers.EMPTY;
    }

    @Override
    public ItemStack extract(long maxQuantity, boolean simulate) {
        return ItemHelpers.EMPTY;
    }

    protected static class SimulatedTileEntityDispenser extends TileEntityDispenser {

        private final IIngredientComponentStorage<ItemStack, Integer> dispenseResultHandler;
        private final IBlockSource blockSource;

        public SimulatedTileEntityDispenser(IIngredientComponentStorage<ItemStack, Integer> dispenseResultHandler,
            IBlockSource blockSource) {
            this.dispenseResultHandler = dispenseResultHandler;
            this.blockSource = blockSource;
        }

        @Override
        public int getSizeInventory() {
            return 0;
        }

        @Override
        public int func_146017_i() {
            return 0;
        }

        @Override
        public int func_146019_a(ItemStack stack) {
            handleDispenseResult(this.dispenseResultHandler, this.blockSource, stack);
            return 0;
        }
    }
}
