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

    public boolean isCreative() {
        IEnergyContainerBlock block = itemBlockEnergyContainer.get();
        return block instanceof BlockEnergyBatteryBase && ((BlockEnergyBatteryBase) block).isCreative();
    }

    @Override
    public int getEnergyStored() {
        if (isCreative()) return Integer.MAX_VALUE;
        NBTTagCompound tag = ItemNBTHelpers.getNBT(itemStack);
        return tag.getInteger(
            itemBlockEnergyContainer.get()
                .getEneryContainerNBTName());
    }

    @Override
    public int getMaxEnergyStored() {
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
    public int receiveEnergy(int energy, boolean simulate) {
        if (isCreative()) return 0;
        energy = Math.min(energy, getRate());
        int stored = getEnergyStored();
        int energyReceived = Math.min(getMaxEnergyStored() - stored, energy);
        if (!simulate) {
            setEnergy(itemStack, stored + energyReceived);
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int energy, boolean simulate) {
        if (isCreative()) return energy;
        energy = Math.min(energy, getRate());
        int stored = getEnergyStored();
        int newEnergy = Math.max(stored - energy, 0);
        if (!simulate) {
            setEnergy(itemStack, newEnergy);
        }
        return stored - newEnergy;
    }

    protected void setEnergy(ItemStack itemStack, int energy) {
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
        tag.setInteger(
            itemBlockEnergyContainer.get()
                .getEneryContainerCapacityNBTName(),
            capacity);
    }
}
