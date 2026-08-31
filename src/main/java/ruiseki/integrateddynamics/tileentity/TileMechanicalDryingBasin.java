package ruiseki.integrateddynamics.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.integrateddynamics.block.BlockMechanicalDryingBasinConfig;
import ruiseki.integrateddynamics.core.recipe.type.RecipeMechanicalDryingBasin;
import ruiseki.integrateddynamics.core.recipe.type.RecipeTypeMechanicalDryingBasinConfig;
import ruiseki.integrateddynamics.core.tileentity.TileMechanicalMachine;
import ruiseki.okcore.capabilities.resolver.SidedCapabilityResolver;
import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.SmartTank;
import ruiseki.okcore.helper.FluidHelpers;
import ruiseki.okcore.helper.InventoryHelpers;
import ruiseki.okcore.inventory.IInventoryFluid;
import ruiseki.okcore.persist.nbt.NBTPersist;
import ruiseki.okcore.recipe.RecipeManager;

/**
 * A part entity for the mechanical drying basin.
 *
 * @author rubensworks
 */
public class TileMechanicalDryingBasin extends TileMechanicalMachine<IInventoryFluid, RecipeMechanicalDryingBasin>
    implements IInventoryFluid {

    private static final int SLOTS = 5;
    private static final int SLOT_INPUT = 0;
    private static final int[] SLOTS_OUTPUT = { 1, 2, 3, 4 };

    private final SmartTank tankIn;
    private final SmartTank tankOut;

    @NBTPersist
    private boolean work = false;

    public TileMechanicalDryingBasin() {
        super(SLOTS);

        this.tankIn = new SmartTank(FluidHelpers.BUCKET_VOLUME * 10);
        this.tankIn.setTileEntity(this);
        this.tankOut = new SmartTank(FluidHelpers.BUCKET_VOLUME * 10);
        this.tankOut.setTileEntity(this);

        // Add fluid tank capability
        this.capabilityCache
            .addCapabilityResolver(SidedCapabilityResolver.create(CapabilityFluidHandler.FLUID_HANDLER, direction -> {
                if (direction == ForgeDirection.DOWN) {
                    return tankOut;
                }
                return tankIn;
            }));
    }

    @Override
    public RecipeManager.CachedCheck<IInventoryFluid, RecipeMechanicalDryingBasin> createCacheUpdater() {
        return RecipeManager.createCheck(RecipeTypeMechanicalDryingBasinConfig._instance.getInstance());
    }

    @Override
    public IFluidHandler getFluidHandler() {
        return tankIn;
    }

    @Override
    public int[] getInputSlots() {
        return new int[] { SLOT_INPUT };
    }

    @Override
    public int[] getOutputSlots() {
        return SLOTS_OUTPUT;
    }

    @Override
    public boolean wasWorking() {
        return work;
    }

    @Override
    public void setWorking(boolean working) {
        this.work = working;
        markDirty();
        onSendUpdate();
    }

    public SmartTank getTankInput() {
        return tankIn;
    }

    public SmartTank getTankOutput() {
        return tankOut;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("tankIn", getTankInput().writeToNBT(new NBTTagCompound()));
        tag.setTag("tankOut", getTankOutput().writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        getTankInput().readFromNBT(tag.getCompoundTag("tankIn"));
        getTankOutput().readFromNBT(tag.getCompoundTag("tankOut"));
    }

    @Override
    public int getRecipeDuration(RecipeMechanicalDryingBasin recipe) {
        return recipe.getDuration();
    }

    @Override
    protected boolean finalizeRecipe(RecipeMechanicalDryingBasin recipe, boolean simulate) {
        // Output items
        ItemStack outputStack = recipe.getOutputItem()
            .copy();
        if (outputStack != null) {
            if (!InventoryHelpers
                .addToInventory(getInventory(), SLOTS_OUTPUT, NonNullList.withSize(1, outputStack), simulate)
                .isEmpty()) {
                return false;
            }
        }

        // Output fluid
        FluidStack outputFluid = recipe.getOutputFluid();
        if (outputFluid != null) {
            if (getTankOutput().fill(outputFluid.copy(), !simulate) != outputFluid.amount) {
                return false;
            }
        }

        // Only consume items if we are not simulating
        if (!simulate) {
            if (!recipe.getInputIngredient()
                .isEmpty()) {
                this.decrStackSize(SLOT_INPUT, 1);
                this.markDirty();
            }
        }

        // Consume fluid
        FluidStack inputFluid = recipe.getInputFluid();
        if (inputFluid != null) {
            if (FluidHelpers.getAmount(getTankInput().drain(inputFluid, !simulate)) != inputFluid.amount) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int getEnergyConsumptionRate() {
        return BlockMechanicalDryingBasinConfig.consumptionRate;
    }

    @Override
    public int getMaxEnergyStored() {
        return BlockMechanicalDryingBasinConfig.capacity;
    }
}
