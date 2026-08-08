package ruiseki.integrateddynamics.capability.energystorage;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import cofh.api.energy.IEnergyStorage;
import ruiseki.integrateddynamics.block.BlockEnergyBatteryConfig;
import ruiseki.integrateddynamics.core.item.ItemBlockEnergyContainer;
import ruiseki.okcore.helper.ItemNBTHelpers;

/**
 * Energy Battery implementation for ItemBlock's.
 * 
 * @author rubensworks
 */
public class EnergyStorageItemBlockEnergyContainer implements IEnergyStorage {

    private final ItemBlockEnergyContainer itemBlockEnergyContainer;
    private final ItemStack itemStack;

    public EnergyStorageItemBlockEnergyContainer(ItemBlockEnergyContainer itemBlockEnergyContainer,
        ItemStack itemStack) {
        this.itemBlockEnergyContainer = itemBlockEnergyContainer;
        this.itemStack = itemStack;
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
        return BlockEnergyBatteryConfig.capacity;
    }

    @Override
    public int receiveEnergy(int energy, boolean simulate) {
        int stored = getEnergyStored();
        int newEnergy = Math.min(stored + energy, getMaxEnergyStored());
        if (!simulate) {
            setEnergy(itemStack, newEnergy);
        }
        return newEnergy - stored;
    }

    @Override
    public int extractEnergy(int energy, boolean simulate) {
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
}
