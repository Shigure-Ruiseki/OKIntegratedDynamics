package ruiseki.integrateddynamics.tileentity;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.integrateddynamics.block.BlockMechanicalSqueezerConfig;
import ruiseki.integrateddynamics.core.recipe.type.RecipeMechanicalSqueezer;
import ruiseki.integrateddynamics.core.recipe.type.RecipeSqueezer;
import ruiseki.integrateddynamics.core.recipe.type.RecipeTypeMechanicalSqueezerConfig;
import ruiseki.integrateddynamics.core.tileentity.TileMechanicalMachine;
import ruiseki.okcore.capabilities.resolver.BasicCapabilityResolver;
import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.fluid.FluidHelpers;
import ruiseki.okcore.fluid.capability.CapabilityFluidHandler;
import ruiseki.okcore.fluid.handler.IFluidHandler;
import ruiseki.okcore.fluid.handler.SmartTank;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.InventoryHelpers;
import ruiseki.okcore.persist.nbt.NBTPersist;
import ruiseki.okcore.recipe.RecipeManager;

public class TileMechanicalSqueezer extends TileMechanicalMachine<IInventory, RecipeMechanicalSqueezer> {

    private static final int SLOTS = 5;
    private static final int SLOT_INPUT = 0;
    private static final int[] SLOTS_OUTPUT = { 1, 2, 3, 4 };
    private static final int TANK_SIZE = FluidHelpers.BUCKET_VOLUME * 100;

    @NBTPersist
    private boolean autoEjectFluids = false;

    private final SmartTank tank;

    @NBTPersist
    private boolean work = false;

    public TileMechanicalSqueezer() {
        super(SLOTS);
        this.tank = new SmartTank(TANK_SIZE);
        this.tank.setTileEntity(this);

        // Add fluid tank capability
        this.capabilityCache.addCapabilityResolver(
            BasicCapabilityResolver.create(CapabilityFluidHandler.FLUID_HANDLER, () -> this.tank));
    }

    @Override
    public RecipeManager.CachedCheck<IInventory, RecipeMechanicalSqueezer> createCacheUpdater() {
        return RecipeManager.createCheck(RecipeTypeMechanicalSqueezerConfig._instance.getInstance());
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

    public SmartTank getTank() {
        return tank;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        this.tank.writeToNBT(tag);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.tank.readFromNBT(tag);
    }

    @Override
    public int getRecipeDuration(RecipeMechanicalSqueezer recipe) {
        return recipe.getDuration();
    }

    @Override
    protected boolean finalizeRecipe(RecipeMechanicalSqueezer recipe, boolean simulate) {
        // 1. Output items
        NonNullList<ItemStack> outputStacks = NonNullList.create();
        for (RecipeSqueezer.ItemStackChance itemStackChance : recipe.getOutputItems()) {
            ItemStack outputStack = itemStackChance.getItemStack()
                .copy();

            if (simulate || itemStackChance.getChance() == 1.0F
                || itemStackChance.getChance() >= worldObj.rand.nextFloat()) {
                InventoryHelpers.addStackToList(outputStacks, outputStack);
            }
        }

        if (!InventoryHelpers.addToInventory(this, SLOTS_OUTPUT, outputStacks, simulate)
            .isEmpty()) {
            return false;
        }

        // 2. Output fluid
        FluidStack outputFluid = recipe.getOutputFluid();
        if (outputFluid != null) {
            if (this.tank.fill(outputFluid.copy(), !simulate) != outputFluid.amount) {
                return false;
            }
        }

        if (!simulate) {
            this.decrStackSize(SLOT_INPUT, 1);
            this.markDirty();
        }

        return true;
    }

    @Override
    public int getEnergyConsumptionRate() {
        return BlockMechanicalSqueezerConfig.consumptionRate;
    }

    @Override
    public int getMaxEnergyStored() {
        return BlockMechanicalSqueezerConfig.capacity;
    }

    @Override
    protected void updateTileEntity() {
        super.updateTileEntity();
        if (!worldObj.isRemote) {
            // Auto-eject fluid
            if (isAutoEjectFluids() && !getTank().isEmpty()) {
                for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
                    IFluidHandler handler = CapabilityHelpers
                        .getCapability(
                            getWorldObj(),
                            getPos().offset(side),
                            CapabilityFluidHandler.FLUID_HANDLER,
                            side.getOpposite())
                        .getOrNull();
                    if (handler != null) {
                        FluidStack fluidStack = getTank().getFluid()
                            .copy();
                        fluidStack.amount = Math
                            .min(BlockMechanicalSqueezerConfig.autoEjectFluidRate, fluidStack.amount);
                        if (handler.fill(fluidStack, false) > 0) {
                            getTank().drain(handler.fill(fluidStack, true), true);
                            break;
                        }
                    }
                }
            }
        }
    }

    public boolean isAutoEjectFluids() {
        return autoEjectFluids;
    }

    public void setAutoEjectFluids(boolean autoEjectFluids) {
        this.autoEjectFluids = autoEjectFluids;
        markDirty();
        onTankChanged();
    }
}
