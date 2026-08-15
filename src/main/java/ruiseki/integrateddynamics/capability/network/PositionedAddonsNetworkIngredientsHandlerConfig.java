package ruiseki.integrateddynamics.capability.network;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integrateddynamics.api.ingredient.capability.IPositionedAddonsNetworkIngredientsHandler;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the positioned addons network handler capability.
 *
 * @author rubensworks
 */
public class PositionedAddonsNetworkIngredientsHandlerConfig
    extends CapabilityConfig<IPositionedAddonsNetworkIngredientsHandler> {

    /**
     * The unique instance.
     */
    public static PositionedAddonsNetworkIngredientsHandlerConfig _instance;

    @CapabilityInject(IPositionedAddonsNetworkIngredientsHandler.class)
    public static Capability<IPositionedAddonsNetworkIngredientsHandler> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public PositionedAddonsNetworkIngredientsHandlerConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "energy_network",
            "A capability for networks that can hold energy.",
            IPositionedAddonsNetworkIngredientsHandler.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
