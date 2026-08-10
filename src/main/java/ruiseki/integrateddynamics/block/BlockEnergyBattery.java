package ruiseki.integrateddynamics.block;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.integrateddynamics.capability.energystorage.IEnergyStorageCapacity;
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
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        int capacityOriginal = BlockEnergyBatteryConfig.capacity;
        int capacity = capacityOriginal;
        int lastCapacity;

        int maxCap = Math.min(BlockEnergyBatteryConfig.maxCreativeCapacity, BlockEnergyBatteryConfig.maxCapacity);

        do {
            ItemStack emptyStack = new ItemStack(item);
            IEnergyStorageCapacity emptyStorage = (IEnergyStorageCapacity) CapabilityHelpers
                .getCapability(emptyStack, CapabilityEnergy.ENERGY)
                .getOrNull();

            if (emptyStorage != null) {
                emptyStorage.setCapacity(capacity);
                list.add(emptyStack);
            }

            ItemStack fullStack = new ItemStack(item);
            IEnergyStorageCapacity fullStorage = (IEnergyStorageCapacity) CapabilityHelpers
                .getCapability(fullStack, CapabilityEnergy.ENERGY)
                .getOrNull();

            if (fullStorage != null) {
                fullStorage.setCapacity(capacity);
                fullStorage.receiveEnergy(capacity, false);
                list.add(fullStack);
            }

            lastCapacity = capacity;
            capacity = capacity << 2;
        } while (capacity <= maxCap && capacity > lastCapacity);
    }

    @Override
    public boolean isCreative() {
        return false;
    }

}
