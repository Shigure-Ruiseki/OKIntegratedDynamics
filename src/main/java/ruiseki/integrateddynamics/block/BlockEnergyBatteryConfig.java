package ruiseki.integrateddynamics.block;

import net.minecraft.item.ItemBlock;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.core.item.ItemBlockEnergyContainer;
import ruiseki.okcore.config.ConfigurableProperty;
import ruiseki.okcore.config.ConfigurableTypeCategory;
import ruiseki.okcore.config.extendedconfig.BlockContainerConfig;

/**
 * Config for {@link BlockEnergyBattery}.
 *
 * @author rubensworks
 */
public class BlockEnergyBatteryConfig extends BlockContainerConfig {

    /**
     * The unique instance.
     */
    public static BlockEnergyBatteryConfig _instance;

    /**
     * The default capacity of an energy battery.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.MACHINE,
        comment = "The default capacity of an energy battery.")
    public static int capacity = 100000;

    /**
     * How much energy per tick it emits when activated.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.MACHINE,
        comment = "How much energy per tick it emits when activated.",
        isCommandable = true)
    public static int energyPerTick = 2000;

    /**
     * Make a new instance.
     */
    public BlockEnergyBatteryConfig() {
        super(IntegratedDynamics._instance, true, "energyBattery", null, BlockEnergyBattery.class);
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockEnergyContainer.class;
    }
}
