package ruiseki.integrateddynamics.core.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.block.IEnergyBattery;
import ruiseki.integrateddynamics.api.block.IEnergyContainerBlock;
import ruiseki.integrateddynamics.capability.energybattery.EnergyBatteryConfig;
import ruiseki.integrateddynamics.capability.energybattery.EnergyBatteryItemBlockEnergyContainer;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.okcore.capabilities.ICapabilityProvider;
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

    protected IEnergyBattery getEnergyBattery(ItemStack itemStack) {
        return CapabilityHelpers.getCapability(itemStack, EnergyBatteryConfig.CAPABILITY)
            .getOrNull();
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        super.addInformation(itemStack, entityPlayer, list, par4);
        IEnergyBattery energyBattery = getEnergyBattery(itemStack);
        int amount = energyBattery.getStoredEnergy();
        int capacity = energyBattery.getMaxStoredEnergy();
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
        IEnergyBattery energyBattery = getEnergyBattery(itemStack);
        double amount = energyBattery.getStoredEnergy();
        double capacity = energyBattery.getMaxStoredEnergy();
        return (capacity - amount) / capacity;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        return new DefaultCapabilityProvider<>(
            () -> EnergyBatteryConfig.CAPABILITY,
            new EnergyBatteryItemBlockEnergyContainer(this, stack));
    }

    /*
     * ------------------ RF API ------------------
     */

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        return getEnergyBattery(container).addEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        return getEnergyBattery(container).consume(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        return getEnergyBattery(container).getStoredEnergy();
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return getEnergyBattery(container).getMaxStoredEnergy();
    }
}
