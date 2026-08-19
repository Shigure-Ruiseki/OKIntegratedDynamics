package ruiseki.integrateddynamics.block;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.capability.energystorage.IEnergyStorageCapacity;
import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.IntegerProperty;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
import ruiseki.okcore.helper.BlockHelpers;
import ruiseki.okcore.helper.CapabilityHelpers;

/**
 * A block that can hold defined variables so that they can be referred to elsewhere in the network.
 *
 * @author rubensworks
 */
public class BlockEnergyBattery extends BlockEnergyBatteryBase {

    @BlockProperty
    public static final IntegerProperty.MetaIntegerProperty FILL = IntegerProperty.createMeta("fill", 0, 3);

    private static BlockEnergyBattery _instance = null;

    /**
     * Get the unique instance.
     *
     * @return The instance.
     */
    public static BlockEnergyBattery getInstance() {
        return _instance;
    }

    /**
     * Make a new block instance.
     *
     */
    public BlockEnergyBattery() {
        super();
        setHardness(5.0F);
        setStepSound(soundTypeMetal);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        if (!BlockHelpers.isValidCreativeTab(this, tab)) return;

        ItemStack itemStack = new ItemStack(this, 1, 0);
        IEnergyStorageCapacity energyStorage = (IEnergyStorageCapacity) CapabilityHelpers
            .getCapability(itemStack, CapabilityEnergy.ENERGY, null)
            .getOrNull();

        if (energyStorage != null) {
            energyStorage.setCapacity(BlockEnergyBatteryConfig.capacity);
            list.add(itemStack.copy());
            fill(energyStorage);
            list.add(itemStack.copy());
        }
    }

    @Override
    public boolean isCreative() {
        return false;
    }

}
