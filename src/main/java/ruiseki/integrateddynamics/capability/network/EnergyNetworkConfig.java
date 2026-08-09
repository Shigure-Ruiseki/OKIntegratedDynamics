package ruiseki.integrateddynamics.capability.network;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integrateddynamics.api.network.IEnergyNetwork;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the energy network capability.
 *
 * @author rubensworks
 *
 */
public class EnergyNetworkConfig extends CapabilityConfig<IEnergyNetwork> {

    /**
     * The unique instance.
     */
    public static EnergyNetworkConfig _instance;

    @CapabilityInject(IEnergyNetwork.class)
    public static Capability<IEnergyNetwork> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public EnergyNetworkConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "energy_network",
            "A capability for networks that can hold energy.",
            IEnergyNetwork.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
