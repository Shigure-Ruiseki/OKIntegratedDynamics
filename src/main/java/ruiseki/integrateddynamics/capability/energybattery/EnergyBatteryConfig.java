package ruiseki.integrateddynamics.capability.energybattery;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integrateddynamics.api.block.IEnergyBattery;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the energy battery capability.
 *
 * @author rubensworks
 *
 */
public class EnergyBatteryConfig extends CapabilityConfig<IEnergyBattery> {

    /**
     * The unique instance.
     */
    public static EnergyBatteryConfig _instance;

    @CapabilityInject(IEnergyBattery.class)
    public static Capability<IEnergyBattery> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public EnergyBatteryConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "energyBatteryCap",
            "Allows storage of energy.",
            IEnergyBattery.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
