package ruiseki.integrateddynamics.core.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyStorage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.block.IEnergyContainerBlock;
import ruiseki.integrateddynamics.capability.energystorage.EnergyStorageItemBlockEnergyContainer;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.tileentity.TileEnergyBattery;
import ruiseki.okcore.capabilities.ICapabilityProvider;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.helper.CapabilityHelpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.item.IInformationProvider;
import ruiseki.okcore.item.ItemBlockNBT;
import ruiseki.okcore.modcompat.capabilities.DefaultCapabilityProvider;

public class ItemBlockEnergyContainer extends ItemBlockNBT implements IEnergyContainerItem {

    private IEnergyContainerBlock block;

    /**
     * Make a new instance.
     *
     * @param block The blockState instance.
     */
    public ItemBlockEnergyContainer(Block block) {
        super(block);
        this.setHasSubtypes(false);
        // Will crash if no valid instance of.
        this.block = (IEnergyContainerBlock) block;
    }

    /**
     * @return The energy container.
     */
    public IEnergyContainerBlock get() {
        return block;
    }

    protected IEnergyStorage getEnergyBattery(ItemStack itemStack) {
        return CapabilityHelpers.getCapability(itemStack, CapabilityEnergy.ENERGY)
            .getOrNull();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        super.addInformation(itemStack, entityPlayer, list, par4);
        IEnergyStorage energyStorage = getEnergyBattery(itemStack);
        int amount = energyStorage.getEnergyStored();
        int capacity = energyStorage.getMaxEnergyStored();
        String line = String.format("%,d", amount) + " / "
            + String.format("%,d", capacity)
            + " "
            + LangHelpers.localize(L10NValues.GENERAL_ENERGY_UNIT);
        list.add(IInformationProvider.ITEM_PREFIX + line);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack itemStack) {
        IEnergyStorage energyStorage = getEnergyBattery(itemStack);
        double amount = energyStorage.getEnergyStored();
        double capacity = energyStorage.getMaxEnergyStored();
        return (capacity - amount) / capacity;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        return new DefaultCapabilityProvider<>(
            () -> CapabilityEnergy.ENERGY,
            new EnergyStorageItemBlockEnergyContainer(this, stack) {

                @Override
                public int getRate() {
                    return TileEnergyBattery.getEnergyPerTick(getMaxEnergyStored());
                }
            });
    }

    /*
     * ------------------ RF API ------------------
     */
    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        return getEnergyBattery(container).receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        return getEnergyBattery(container).extractEnergy(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        return getEnergyBattery(container).getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return getEnergyBattery(container).getMaxEnergyStored();
    }
}
