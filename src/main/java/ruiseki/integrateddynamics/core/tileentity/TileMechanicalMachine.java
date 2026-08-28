package ruiseki.integrateddynamics.core.tileentity;

import java.util.Set;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Sets;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyStorage;
import lombok.experimental.Delegate;
import ruiseki.integrateddynamics.api.network.IEnergyNetwork;
import ruiseki.integrateddynamics.api.network.INetworkElement;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetwork;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderConfig;
import ruiseki.integrateddynamics.capability.networkelementprovider.NetworkElementProviderSingleton;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.integrateddynamics.network.MechanicalMachineNetworkElement;
import ruiseki.okcore.capabilities.resolver.BasicCapabilityResolver;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.datastructure.LazyOptional;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.energy.component.EnergyHandlerComponent;
import ruiseki.okcore.energy.component.IEnergyHandlerExclusion;
import ruiseki.okcore.fluid.handler.SmartTank;
import ruiseki.okcore.persist.nbt.NBTPersist;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.RecipeManager;

public abstract class TileMechanicalMachine<I extends IInventory, R extends IRecipeOK<I>>
    extends TileCableConnectableInventory implements IEnergyStorage, IEnergyHandler, SmartTank.IUpdateListener {

    /**
     * The number of ticks to sleep when the recipe could not be finalized.
     */
    private static int SLEEP_TIME = 40;

    @NBTPersist
    private int energy;
    @NBTPersist
    private int progress = -1;
    @NBTPersist
    private int sleep = -1;

    private RecipeManager.CachedCheck<I, R> recipeCache;

    @Delegate(excludes = IEnergyHandlerExclusion.class)
    private final EnergyHandlerComponent energyHandlerComponent = new EnergyHandlerComponent(this);

    public TileMechanicalMachine(int inventorySize) {
        super(inventorySize, "machine", 64);

        // Add energy capability
        this.capabilityCache.addCapabilityResolver(
            BasicCapabilityResolver
                .create(NetworkElementProviderConfig.CAPABILITY, () -> new NetworkElementProviderSingleton() {

                    @Override
                    public INetworkElement createNetworkElement(World world, BlockPos blockPos) {
                        return new MechanicalMachineNetworkElement(DimPos.of(world, blockPos));
                    }
                }));
        this.capabilityCache.addCapabilityResolver(BasicCapabilityResolver.create(CapabilityEnergy.ENERGY, () -> this));

        // Set inventory sides
        Set<Integer> in = Sets.newHashSet(ArrayUtils.toObject(getInputSlots()));
        Set<Integer> out = Sets.newHashSet(ArrayUtils.toObject(getOutputSlots()));
        addSlotsToSide(ForgeDirection.UP, in);
        addSlotsToSide(ForgeDirection.DOWN, out);
        addSlotsToSide(ForgeDirection.NORTH, in);
        addSlotsToSide(ForgeDirection.SOUTH, out);
        addSlotsToSide(ForgeDirection.WEST, in);
        addSlotsToSide(ForgeDirection.EAST, out);
        this.recipeCache = createCacheUpdater();
    }

    /**
     * @return A new cache updater instance.
     */
    public abstract RecipeManager.CachedCheck<I, R> createCacheUpdater();

    /**
     * @return The available input slots.
     */
    public abstract int[] getInputSlots();

    /**
     * @return The available output slots.
     */
    public abstract int[] getOutputSlots();

    /**
     * @return If the machine was in a working state.
     */
    public abstract boolean wasWorking();

    /**
     * Set the new working state.
     *
     * @param working If the machine is working.
     */
    public abstract void setWorking(boolean working);

    /**
     * @return If the machine currently has any work to process.
     */
    public boolean hasWork() {
        return getCurrentRecipe() != null;
    }

    /**
     * @return If the machine is currently working.
     */
    public boolean isWorking() {
        return this.progress >= 0 && this.sleep == -1;
    }

    /**
     * @return If the machine is able to work in its current state.
     *         This for example takes into account the available energy.
     */
    public boolean canWork() {
        int rate = getEnergyConsumptionRate();
        return drainEnergy(rate, true) == rate && !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
    }

    /**
     * @return If the machine is currently sleeping due to a recipe that could not be finalized.
     */
    public boolean isSleeping() {
        return this.sleep > 0;
    }

    public LazyOptional<IEnergyNetwork> getEnergyNetwork() {
        return NetworkHelpers.getEnergyNetwork(getNetwork());
    }

    @Override
    public void onTankChanged() {
        markDirty();
    }

    @Override
    protected void onInventoryChanged() {
        super.onInventoryChanged();
        this.sleep = -1;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return ArrayUtils.contains(getInputSlots(), index) && super.isItemValidForSlot(index, stack);
    }

    /**
     * @return The currently applicable recipe.
     */
    @SuppressWarnings("unchecked")
    public R getCurrentRecipe() {
        return recipeCache.getRecipeFor((I) this, worldObj)
            .orElse(null);
    }

    /**
     * @return The current recipe progress, going from 0 to maxProgress.
     */
    public int getProgress() {
        return progress;
    }

    /**
     * @return The current maximum progress.
     */
    public int getMaxProgress() {
        return this.getCurrentRecipe() != null ? getRecipeDuration(getCurrentRecipe()) : 0;
    }

    /**
     * @param recipe A recipe.
     * @return The duration of a given recipe.
     */
    public abstract int getRecipeDuration(R recipe);

    /**
     * Finalize a recipe.
     * This should insert the recipe output in the machine, and consume the input.
     * If the output could not be added, this method should return false.
     *
     * @param recipe   A recipe.
     * @param simulate If finalization should be simulated.
     * @return If finalization was successful.
     */
    protected abstract boolean finalizeRecipe(R recipe, boolean simulate);

    @Override
    protected void updateTileEntity() {
        super.updateTileEntity();
        if (!worldObj.isRemote) {
            if (isSleeping()) {
                this.sleep--;
                this.markDirty();
            } else if (canWork()) {
                R recipe = getCurrentRecipe();
                if (recipe != null) {
                    if (progress == 0 && !finalizeRecipe(recipe, true)) {
                        sleep = SLEEP_TIME;
                    } else if (progress < getMaxProgress()) {
                        // // Consume energy while progressing
                        int toDrain = getEnergyConsumptionRate();
                        if (drainEnergy(toDrain, true) == toDrain) {
                            drainEnergy(toDrain, false);
                            progress++;
                            sleep = -1;
                        } else {
                            sleep = 1;
                        }
                    } else {
                        // Otherwise, finish and output

                        // First check if we have enough room for the recipe output,
                        // if not, we sleep for a while.
                        if (finalizeRecipe(recipe, true)) {
                            progress = 0;
                            finalizeRecipe(recipe, false);
                        } else {
                            sleep = 40;
                        }
                    }
                } else {
                    this.progress = -1;
                    this.sleep = -1;
                }
            }

            // Check if a state update is needed.
            updateWorkingState();
        }
    }

    /**
     * Update the working state.
     */
    public void updateWorkingState() {
        boolean wasWorking = wasWorking();
        boolean isWorking = isWorking();
        if (isWorking != wasWorking) {
            setWorking(isWorking);
        }
    }

    /**
     * @return The energy consumption rate per (working) tick.
     */
    public abstract int getEnergyConsumptionRate();

    /**
     * Drain energy from the internal buffer or the attached network.
     *
     * @param amount   The amount of energy.
     * @param simulate If drainage should be simulated.
     * @return The drained energy.
     */
    protected int drainEnergy(int amount, boolean simulate) {
        int toDrain = amount;

        // First, check internal buffer
        toDrain -= this.extractEnergyInternal(toDrain, simulate);

        if (toDrain > 0) {
            // If we still need energy, ask it from the network.
            IEnergyNetwork energyNetwork = getEnergyNetwork().getOrNull();
            if (energyNetwork != null) {
                toDrain -= energyNetwork.getChannel(IPositionedAddonsNetwork.DEFAULT_CHANNEL)
                    .extract(toDrain, simulate);
            }
        }
        return amount - toDrain;
    }

    protected int extractEnergyInternal(int energy, boolean simulate) {
        energy = Math.max(0, energy);
        int stored = getEnergyStored();
        int newEnergy = Math.max(stored - energy, 0);
        if (!simulate) {
            setEnergy(newEnergy);
        }
        return stored - newEnergy;
    }

    protected void setEnergy(int energy) {
        int lastEnergy = this.energy;
        if (lastEnergy != energy) {
            this.energy = energy;
            markDirty();
        }
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int stored = getEnergyStored();
        int energyReceived = Math.min(getMaxEnergyStored() - stored, maxReceive);
        if (!simulate) {
            setEnergy(stored + energyReceived);
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return this.energy;
    }
}
