package ruiseki.integrateddynamics.block;

import net.minecraft.item.ItemBlock;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.core.item.ItemBlockEnergyContainerAutoSupply;
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
        comment = "The default capacity of an energy battery.",
        minimalValue = 0)
    public static int capacity = 1000000;

    /**
     * The 1/X fraction of the battery capacity that is allowed to be transfered per tick.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.MACHINE,
        comment = "The 1/X fraction of the battery capacity that is allowed to be transfered per tick.",
        isCommandable = true,
        minimalValue = 0)
    public static int energyRateCapacityFraction = 2000;

    /**
     * The minimum energy transfer rate per tick.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.MACHINE,
        comment = "The minimum energy transfer rate per tick.",
        isCommandable = true,
        minimalValue = 0)
    public static int minEnergyRate = 2000;

    /**
     * The maximum capacity possible by combining batteries.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.MACHINE,
        comment = "The maximum capacity possible by combining batteries. (Make sure that you do not cross the max int size.)")
    public static int maxCapacity = 655360000;
    /**
     * The maximum capacity visible in the creative tabs.
     */
    @ConfigurableProperty(
        category = ConfigurableTypeCategory.MACHINE,
        comment = "The maximum capacity visible in the creative tabs. (Make sure that you do not cross the max int size.)")
    public static int maxCreativeCapacity = 40960000;

    /**
     * Make a new instance.
     */
    public BlockEnergyBatteryConfig() {
        super(IntegratedDynamics._instance, true, "energy_battery", null, BlockEnergyBattery.class);
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockEnergyContainerAutoSupply.class;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onRegistered() {
        super.onRegistered();
    }
}
