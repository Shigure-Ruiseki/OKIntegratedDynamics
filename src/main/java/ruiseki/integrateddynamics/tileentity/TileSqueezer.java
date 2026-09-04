package ruiseki.integrateddynamics.tileentity;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import com.google.common.collect.Sets;

import lombok.Getter;
import lombok.experimental.Delegate;
import ruiseki.integrateddynamics.block.BlockSqueezer;
import ruiseki.integrateddynamics.core.recipe.type.RecipeSqueezer;
import ruiseki.integrateddynamics.core.recipe.type.RecipeTypeSqueezerConfig;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.helper.BlockStateHelpers;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.FluidHelpers;
import ruiseki.okcore.helper.ItemHandlerHelpers;
import ruiseki.okcore.helper.ItemHelpers;
import ruiseki.okcore.item.capability.CapabilityItemHandler;
import ruiseki.okcore.item.handler.IItemHandler;
import ruiseki.okcore.persist.nbt.NBTPersist;
import ruiseki.okcore.recipe.RecipeManager;
import ruiseki.okcore.tileentity.TankInventoryTileEntity;
import ruiseki.okcore.tileentity.TileEntityOK;

/**
 * A part entity for squeezing stuff.
 *
 * @author rubensworks
 */
public class TileSqueezer extends TankInventoryTileEntity implements TileEntityOK.ITickingTile {

    @Delegate
    private final ITickingTile tickingTileComponent = new TickingTileComponent(this);

    @NBTPersist
    @Getter
    private int itemHeight = 1;

    private BlockSqueezer.EnumAxis axis = BlockSqueezer.EnumAxis.X;

    @NBTPersist
    @Getter
    private int height = 1;

    private final RecipeManager.CachedCheck<IInventory, RecipeSqueezer> recipeCache = RecipeManager
        .createCheck(RecipeTypeSqueezerConfig._instance.getInstance());

    public TileSqueezer() {
        super(1, "squeezerInventory", 1, FluidHelpers.BUCKET_VOLUME);

        addSlotsToSide(ForgeDirection.UP, Sets.newHashSet(0));
        addSlotsToSide(ForgeDirection.DOWN, Sets.newHashSet(0));
        addSlotsToSide(ForgeDirection.NORTH, Sets.newHashSet(0));
        addSlotsToSide(ForgeDirection.SOUTH, Sets.newHashSet(0));
        addSlotsToSide(ForgeDirection.WEST, Sets.newHashSet(0));
        addSlotsToSide(ForgeDirection.EAST, Sets.newHashSet(0));
    }

    public RecipeSqueezer getCurrentRecipe() {
        return recipeCache.getRecipeFor(this, worldObj)
            .orElse(null);
    }

    public void setAxis(BlockSqueezer.EnumAxis axis) {
        this.axis = axis;
        markDirty();
        onSendUpdate();
    }

    public BlockSqueezer.EnumAxis getAxis() {
        return axis;
    }

    public void setHeight(int height) {
        this.height = height;
        markDirty();
        onSendUpdate();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("axis", axis.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.axis = BlockSqueezer.EnumAxis.getOrientation(tag.getInteger("axis"));
    }

    @Override
    public boolean isSendUpdateOnInventoryChanged() {
        return true;
    }

    @Override
    protected void updateTileEntity() {
        super.updateTileEntity();
        if (!getWorldObj().isRemote) {
            if (!getTank().isEmpty()) {
                ForgeDirection[] sides = BlockStateHelpers.get(worldObj, pos, BlockSqueezer.AXIS)
                    .getSides();
                for (ForgeDirection side : sides) {
                    IFluidHandler handler = CapabilityHelpers
                        .getCapability(
                            getWorldObj(),
                            getPos().offset(side),
                            CapabilityFluidHandler.FLUID_HANDLER,
                            side.getOpposite())
                        .getOrNull();
                    if (!getTank().isEmpty() && handler != null) {
                        FluidStack fluidStack = new FluidStack(
                            getTank().getFluid(),
                            Math.min(100, getTank().getFluidAmount()));
                        if (handler.fill(fluidStack, false) > 0) {
                            int filled = handler.fill(fluidStack, true);
                            drain(filled, true);
                        }
                    }
                }
            } else {
                if (itemHeight == 7) {
                    RecipeSqueezer recipe = getCurrentRecipe();
                    if (recipe != null) {
                        setInventorySlotContents(0, null);

                        for (RecipeSqueezer.ItemStackChance output : recipe.getOutputItems()) {
                            if (output.getChance() == 1.0F || output.getChance() >= worldObj.rand.nextFloat()) {
                                ItemStack resultStack = output.getItemStack()
                                    .copy();

                                for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                                    if (resultStack != null && resultStack.stackSize > 0 && side != ForgeDirection.UP) {
                                        IItemHandler itemHandler = CapabilityHelpers
                                            .getCapability(
                                                getWorldObj(),
                                                getPos().offset(side),
                                                CapabilityItemHandler.ITEM_HANDLER,
                                                side.getOpposite())
                                            .orElse(null);
                                        if (itemHandler != null) {
                                            resultStack = ItemHandlerHelpers
                                                .insertItem(itemHandler, resultStack, false);
                                        }
                                    }
                                }

                                if (resultStack != null && resultStack.stackSize > 0) {
                                    ItemHelpers.spawnItemStack(worldObj, xCoord, yCoord, zCoord, resultStack);
                                }
                            }
                        }

                        if (recipe.getOutputFluid() != null) {
                            fill(
                                recipe.getOutputFluid()
                                    .copy(),
                                true);
                        }

                        markDirty();
                        sendUpdate();
                    }
                }
            }
        }
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
        return BlockStateHelpers.get(worldObj, pos, BlockSqueezer.HEIGHT) == 1 && getStackInSlot(0) == null
            && super.canInsertItem(slot, itemStack, side);
    }

    @Override
    public void setInventorySlotContents(int slotId, ItemStack itemstack) {
        super.setInventorySlotContents(slotId, itemstack);
        itemHeight = 1;
        sendUpdate();
    }

    public void setItemHeight(int itemHeight) {
        this.itemHeight = itemHeight;
        sendUpdate();
    }
}
