package ruiseki.integrateddynamics.block;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.helper.BlockHelpers;
import ruiseki.okcore.helper.CapabilityHelpers;

/**
 * A block that can hold defined variables so that they can be referred to elsewhere in the network.
 *
 * @author rubensworks
 */
public class BlockCreativeEnergyBattery extends BlockEnergyBatteryBase {

    /**
     * Make a new block instance.
     */
    public BlockCreativeEnergyBattery() {
        super();

        setHardness(5.0F);
        setStepSound(soundTypeMetal);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        if (!BlockHelpers.isValidCreativeTab(this, tab)) return;
        ItemStack full = new ItemStack(this);
        CapabilityHelpers.getCapability(full, CapabilityEnergy.ENERGY, null)
            .ifPresent(energyStorage -> {
                fill(energyStorage);
                list.add(full);
            });
    }

    public boolean isCreative() {
        return true;
    }

}
