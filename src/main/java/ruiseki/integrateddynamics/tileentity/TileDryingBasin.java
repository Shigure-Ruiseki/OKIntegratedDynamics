package ruiseki.integrateddynamics.tileentity;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Sets;

import lombok.experimental.Delegate;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.core.recipe.type.RecipeDryingBasin;
import ruiseki.integrateddynamics.core.recipe.type.RecipeTypeDryingBasinConfig;
import ruiseki.okcore.fluid.FluidHelpers;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.inventory.IInventoryFluid;
import ruiseki.okcore.persist.nbt.NBTPersist;
import ruiseki.okcore.recipe.RecipeManager;
import ruiseki.okcore.tileentity.TankInventoryTileEntity;
import ruiseki.okcore.tileentity.TileEntityOK;

public class TileDryingBasin extends TankInventoryTileEntity implements TileEntityOK.ITickingTile, IInventoryFluid {

    private static final int WOOD_IGNITION_TEMPERATURE = 573; // 300 degrees celcius

    @Delegate
    private final TileEntityOK.ITickingTile tickingTileComponent = new TileEntityOK.TickingTileComponent(this);

    @NBTPersist
    private Float randomRotation = 0F;
    @NBTPersist
    private int progress = 0;
    @NBTPersist
    private int fire = 0;

    private final RecipeManager.CachedCheck<IInventoryFluid, RecipeDryingBasin> recipeCache = RecipeManager
        .createCheck(RecipeTypeDryingBasinConfig._instance.getInstance());

    public TileDryingBasin() {
        super(1, "dryingBasingInventory", 1, FluidHelpers.BUCKET_VOLUME);

        addSlotsToSide(ForgeDirection.UP, Sets.newHashSet(0));
        addSlotsToSide(ForgeDirection.DOWN, Sets.newHashSet(0));
        addSlotsToSide(ForgeDirection.NORTH, Sets.newHashSet(0));
        addSlotsToSide(ForgeDirection.SOUTH, Sets.newHashSet(0));
        addSlotsToSide(ForgeDirection.WEST, Sets.newHashSet(0));
        addSlotsToSide(ForgeDirection.EAST, Sets.newHashSet(0));
    }

    public RecipeDryingBasin getCurrentRecipe() {
        return recipeCache.getRecipeFor(this, worldObj)
            .orElse(null);
    }

    @Override
    public void onTankChanged() {
        super.onTankChanged();
        if (worldObj != null && !worldObj.isRemote) {
            this.progress = 0;
        }
    }

    @Override
    public boolean isSendUpdateOnInventoryChanged() {
        return true;
    }

    @Override
    protected void updateTileEntity() {
        super.updateTileEntity();
        if (!worldObj.isRemote) {
            if (!getTank().isEmpty() && getTank().getFluid()
                .getFluid()
                .getTemperature(getTank().getFluid()) >= WOOD_IGNITION_TEMPERATURE) {

                if (++fire >= 100) {
                    getWorldObj().setBlock(xCoord, yCoord, zCoord, Blocks.fire);
                } else if (getWorldObj().isAirBlock(xCoord, yCoord + 1, zCoord) && worldObj.rand.nextInt(10) == 0) {
                    getWorldObj().setBlock(xCoord, yCoord + 1, zCoord, Blocks.fire);
                }

            } else {
                RecipeDryingBasin recipe = getCurrentRecipe();
                if (recipe != null) {
                    if (progress >= recipe.getDuration()) {
                        ItemStack output = recipe.getOutputItem();
                        if (output != null) {
                            output = output.copy();
                            setInventorySlotContents(0, output);
                            int amount = FluidHelpers.getAmount(recipe.getInputFluid());
                            drain(amount, true);
                            if (recipe.getOutputFluid() != null) {
                                if (fill(recipe.getOutputFluid(), true) == 0) {
                                    IntegratedDynamics
                                        .clog(Level.ERROR, "Encountered an invalid recipe: " + recipe.getId());
                                }
                            }
                        }
                        progress = 0;
                    } else {
                        progress++;
                        markDirty();
                    }
                    fire = 0;
                } else {
                    if ((progress > 0) || (fire > 0)) {
                        progress = 0;
                        fire = 0;
                        markDirty();
                    }
                }
            }
        } else if (progress > 0 && getWorldObj().rand.nextInt(5) == 0) {
            if (!getTank().isEmpty()) {
                Block block = getTank().getFluid()
                    .getFluid()
                    .getBlock();
                if (block != null) {
                    int blockId = Block.getIdFromBlock(block);
                    int metadata = 0;
                    getWorldObj().spawnParticle(
                        "tilecrack_" + blockId + "_" + metadata,
                        xCoord + Math.random() * 0.8D + 0.1D,
                        yCoord + Math.random() * 0.1D + 0.9D,
                        zCoord + Math.random() * 0.8D + 0.1D,
                        0.0D,
                        0.1D,
                        0.0D);
                }
            }

            ItemStack stack = getStackInSlot(0);
            if (stack != null) {
                int itemId = Item.getIdFromItem(stack.getItem());
                int damage = stack.getItemDamage();
                getWorldObj().spawnParticle(
                    "iconcrack_" + itemId + "_" + damage,
                    xCoord + Math.random() * 0.8D + 0.1D,
                    yCoord + Math.random() * 0.1D + 0.9D,
                    zCoord + Math.random() * 0.8D + 0.1D,
                    0.0D,
                    0.1D,
                    0.0D);
            }
        }
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
        return getStackInSlot(0) == null;
    }

    @Override
    public void setInventorySlotContents(int slotId, ItemStack itemstack) {
        super.setInventorySlotContents(slotId, itemstack);
        this.progress = 0;
        this.randomRotation = worldObj.rand.nextFloat() * 360;
        sendUpdate();
    }

    public float getRandomRotation() {
        return randomRotation;
    }

    @Override
    public IFluidHandler getFluidHandler() {
        return getTank();
    }
}
