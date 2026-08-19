package ruiseki.integrateddynamics.block;

import net.minecraft.item.ItemBlock;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.core.item.ItemBlockEnergyContainerAutoSupply;
import ruiseki.okcore.config.extendedconfig.BlockContainerConfig;

/**
 * Config for {@link BlockCreativeEnergyBattery}.
 *
 * @author rubensworks
 */
public class BlockCreativeEnergyBatteryConfig extends BlockContainerConfig {

    /**
     * The unique instance.
     */
    public static BlockCreativeEnergyBatteryConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockCreativeEnergyBatteryConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "creative_energy_battery",
            null,
            config -> new BlockCreativeEnergyBattery());
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockEnergyContainerAutoSupply.class;
    }
}
