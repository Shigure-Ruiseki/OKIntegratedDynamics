package ruiseki.integrateddynamics.capability.energystorage;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import ruiseki.integrateddynamics.block.BlockEnergyBatteryConfig;
import ruiseki.integrateddynamics.core.item.ItemBlockEnergyContainer;
import ruiseki.okcore.helper.ItemNBTHelpers;

/**
 * Energy Battery implementation for ItemBlock's.
 *
 * @author rubensworks
 */
public class EnergyStorageItemBlockEnergyContainer implements IEnergyStorageCapacity {

    private final ItemBlockEnergyContainer itemBlockEnergyContainer;
    private final ItemStack itemStack;
    private final int rate;

    public EnergyStorageItemBlockEnergyContainer(ItemBlockEnergyContainer itemBlockEnergyContainer, ItemStack itemStack,
        int rate) {
        this.itemBlockEnergyContainer = itemBlockEnergyContainer;
        this.itemStack = itemStack;
        this.rate = rate;
    }

    public EnergyStorageItemBlockEnergyContainer(ItemBlockEnergyContainer itemBlockEnergyContainer,
        ItemStack itemStack) {
        this(itemBlockEnergyContainer, itemStack, Integer.MAX_VALUE);
    }

    public int getRate() {
        return rate;
    }

    @Override
    public int getEnergyStored() {
        NBTTagCompound tag = ItemNBTHelpers.getNBT(itemStack);
        return tag.getInteger(
            itemBlockEnergyContainer.get()
                .getEneryContainerNBTName());
    }

    @Override
    public int getMaxEnergyStored() {
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
    public int receiveEnergy(int energy, boolean simulate) {
        energy = Math.min(energy, getRate());
        int stored = getEnergyStored();
        int newEnergy = Math.min(stored + energy, getMaxEnergyStored());
        if (!simulate) {
            setEnergy(itemStack, newEnergy);
        }
        return newEnergy - stored;
    }

    @Override
    public int extractEnergy(int energy, boolean simulate) {
        energy = Math.min(energy, getRate());
        int stored = getEnergyStored();
        int newEnergy = Math.max(stored - energy, 0);
        if (!simulate) {
            setEnergy(itemStack, newEnergy);
        }
        return stored - newEnergy;
    }

    protected void setEnergy(ItemStack itemStack, int energy) {
        NBTTagCompound tag = ItemNBTHelpers.getNBT(itemStack);
        tag.setInteger(
            itemBlockEnergyContainer.get()
                .getEneryContainerNBTName(),
            energy);
    }

    @Override
    public void setCapacity(int capacity) {
        NBTTagCompound tag = ItemNBTHelpers.getNBT(itemStack);
        tag.setInteger(
            itemBlockEnergyContainer.get()
                .getEneryContainerCapacityNBTName(),
            capacity);
    }
}
