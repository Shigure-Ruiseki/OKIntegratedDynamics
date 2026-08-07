package ruiseki.integrateddynamics.core.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.api.block.IEnergyContainer;
import ruiseki.integrateddynamics.api.block.IEnergyContainerBlock;
import ruiseki.integrateddynamics.block.BlockEnergyBatteryConfig;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.okcore.helper.ItemNBTHelpers;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.item.IInformationProvider;
import ruiseki.okcore.item.ItemBlockNBT;

public class ItemBlockEnergyContainer extends ItemBlockNBT implements IEnergyContainer, IEnergyContainerItem {

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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4) {
        super.addInformation(itemStack, entityPlayer, list, par4);
        int amount = getStoredEnergy(itemStack);
        int capacity = getMaxStoredEnergy(itemStack);
        String line = String.format("%,d", amount) + " / "
            + String.format("%,d", capacity)
            + " "
            + LangHelpers.localize(L10NValues.GENERAL_ENERGY_UNIT);
        list.add(IInformationProvider.ITEM_PREFIX + line);
    }

    @Override
    public int getStoredEnergy(ItemStack itemStack) {
        NBTTagCompound tag = ItemNBTHelpers.getNBT(itemStack);
        return tag.getInteger(get().getEneryContainerNBTName());
    }

    @Override
    public int getMaxStoredEnergy(ItemStack itemStack) {
        return BlockEnergyBatteryConfig.capacity;
    }

    protected void setEnergy(ItemStack itemStack, int energy) {
        NBTTagCompound tag = ItemNBTHelpers.getNBT(itemStack);
        tag.setInteger(get().getEneryContainerNBTName(), energy);
    }

    @Override
    public int addEnergy(ItemStack itemStack, int energy, boolean simulate) {
        int stored = getStoredEnergy(itemStack);
        int newEnergy = Math.min(stored + energy, getMaxStoredEnergy(itemStack));
        if (!simulate) {
            setEnergy(itemStack, newEnergy);
        }
        return newEnergy - stored;
    }

    @Override
    public int consume(ItemStack itemStack, int energy, boolean simulate) {
        int stored = getStoredEnergy(itemStack);
        int newEnergy = Math.max(stored - energy, 0);
        if (!simulate) {
            setEnergy(itemStack, newEnergy);
        }
        return stored - newEnergy;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack itemStack) {
        double amount = getStoredEnergy(itemStack);
        double capacity = getMaxStoredEnergy(itemStack);
        return (capacity - amount) / capacity;
    }

    /*
     * ------------------ RF API ------------------
     */

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        return addEnergy(container, maxReceive, simulate);
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        return consume(container, maxExtract, simulate);
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        return getStoredEnergy(container);
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return getMaxStoredEnergy(container);
    }
}
