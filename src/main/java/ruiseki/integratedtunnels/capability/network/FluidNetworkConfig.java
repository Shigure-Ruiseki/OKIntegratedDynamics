package ruiseki.integratedtunnels.capability.network;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integratedtunnels.api.network.IFluidNetwork;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the item network capability.
 * 
 * @author rubensworks
 *
 */
public class FluidNetworkConfig extends CapabilityConfig<IFluidNetwork> {

    /**
     * The unique instance.
     */
    public static FluidNetworkConfig _instance;

    @CapabilityInject(IFluidNetwork.class)
    public static Capability<IFluidNetwork> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public FluidNetworkConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "fluidNetwork",
            "A capability for networks that can hold fluids.",
            IFluidNetwork.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
