package ruiseki.integrateddynamics.capability.energystorage;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import ruiseki.integrateddynamics.block.BlockEnergyBatteryBase;
import ruiseki.integrateddynamics.block.BlockEnergyBatteryConfig;
import ruiseki.integrateddynamics.block.IEnergyContainerBlock;
import ruiseki.integrateddynamics.core.item.ItemBlockEnergyContainer;
import ruiseki.okcore.helper.ItemNBTHelpers;

/**
 * Energy Battery implementation for ItemBlock's.
 *
 * @author rubensworks
 */
public class EnergyStorageItemBlockEnergyContainer implements IEnergyStorageCapacity, IEnergyStorageMutable {

    private final ItemBlockEnergyContainer itemBlockEnergyContainer;
    private final ItemStack itemStack;
    private final int rate;

    public EnergyStorageItemBlockEnergyContainer(ItemBlockEnergyContainer itemBlockEnergyContainer, ItemStack itemStack,
        int rate) {
        this.itemBlockEnergyContainer = itemBlockEnergyContainer;
        this.itemStack = itemStack;
        this.rate = rate;

        if (!this.itemStack.hasTagCompound()) {
            setItemStackEnergy(itemStack, 0);
        }
    }

    public EnergyStorageItemBlockEnergyContainer(ItemBlockEnergyContainer itemBlockEnergyContainer,
        ItemStack itemStack) {
        this(itemBlockEnergyContainer, itemStack, Integer.MAX_VALUE);
    }

    public int getRate() {
        return rate;
    }

    public boolean isCreative() {
        IEnergyContainerBlock block = itemBlockEnergyContainer.get();
        return block instanceof BlockEnergyBatteryBase && ((BlockEnergyBatteryBase) block).isCreative();
    }

    protected int getEnergyStoredSingular() {
        if (isCreative()) return Integer.MAX_VALUE;
        NBTTagCompound tag = ItemNBTHelpers.getNBT(itemStack);
        return tag.getInteger(
            itemBlockEnergyContainer.get()
                .getEneryContainerNBTName());
    }

    @Override
    public int getEnergyStored() {
        return getEnergyStoredSingular() * this.itemStack.stackSize;
    }

    public int getMaxEnergyStoredSingular() {
        if (isCreative()) return Integer.MAX_VALUE;
        NBTTagCompound tag = ItemNBTHelpers.getNBT(itemStack);
        if (!tag.hasKey(
            itemBlockEnergyContainer.get()
                .getEneryContainerCapacityNBTName())) {
            return BlockEnergyBatteryConfig.capacity;
        }
        return tag.getInteger(
            itemBlockEnergyContainer.get()
                .getEneryContainerCapacityNBTName());
    }

    @Override
    public int getMaxEnergyStored() {
        return getMaxEnergyStoredSingular() * this.itemStack.stackSize;
    }

    @Override
    public int receiveEnergy(int energy, boolean simulate) {
        if (isCreative()) return 0;
        int stackSize = this.itemStack.stackSize;
        if (stackSize == 0) return 0;
        energy /= stackSize;
        energy = Math.min(energy, getRate());
        int stored = getEnergyStoredSingular();
        int energyReceived = Math.min(getMaxEnergyStoredSingular() - stored, energy);
        if (!simulate) {
            setItemStackEnergy(itemStack, stored + energyReceived);
        }
        return energyReceived * stackSize;
    }

    @Override
    public int extractEnergy(int energy, boolean simulate) {
        if (isCreative()) return energy;
        int stackSize = this.itemStack.stackSize;
        if (stackSize == 0) return energy;
        energy /= stackSize;
        energy = Math.min(energy, getRate());
        int stored = getEnergyStoredSingular();
        int newEnergy = Math.max(stored - energy, 0);
        if (!simulate) {
            setItemStackEnergy(itemStack, newEnergy);
        }
        return (stored - newEnergy) * stackSize;
    }

    public void setItemStackEnergy(ItemStack itemStack, int energy) {
        if (isCreative()) return;
        NBTTagCompound tag = ItemNBTHelpers.getNBT(itemStack);
        tag.setInteger(
            itemBlockEnergyContainer.get()
                .getEneryContainerNBTName(),
            energy);
    }

    @Override
    public void setCapacity(int capacity) {
        NBTTagCompound tag = ItemNBTHelpers.getNBT(itemStack);
        if (capacity == BlockEnergyBatteryConfig.capacity) {
            tag.removeTag(
                itemBlockEnergyContainer.get()
                    .getEneryContainerCapacityNBTName());
        } else {
            tag.setInteger(
                itemBlockEnergyContainer.get()
                    .getEneryContainerCapacityNBTName(),
                capacity);
        }
    }

    @Override
    public void setEnergy(int energy) {
        setItemStackEnergy(itemStack, energy);
    }
}
