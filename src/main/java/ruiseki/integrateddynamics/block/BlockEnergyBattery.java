package ruiseki.integrateddynamics.block;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.okcore.block.property.BlockProperty;
import ruiseki.okcore.block.property.IntegerProperty;
import ruiseki.okcore.config.extendedconfig.ExtendedConfig;
import ruiseki.okcore.energy.capability.CapabilityEnergy;
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
     * @param eConfig Config for this block.
     */
    public BlockEnergyBattery(ExtendedConfig eConfig) {
        super(eConfig);

        setHardness(5.0F);
        setStepSound(soundTypeMetal);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        ItemStack full = new ItemStack(this);
        CapabilityHelpers.getCapability(full, CapabilityEnergy.ENERGY)
            .ifPresent(handler -> {
                handler.receiveEnergy(handler.getMaxEnergyStored(), false);
                list.add(full);
            });
    }

    @Override
    public boolean isCreative() {
        return false;
    }

}
