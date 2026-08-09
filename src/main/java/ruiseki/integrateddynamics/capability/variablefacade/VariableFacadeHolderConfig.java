package ruiseki.integrateddynamics.capability.variablefacade;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integrateddynamics.api.item.IVariableFacadeHolder;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the variable facade holder capability.
 *
 * @author rubensworks
 *
 */
public class VariableFacadeHolderConfig extends CapabilityConfig<IVariableFacadeHolder> {

    /**
     * The unique instance.
     */
    public static VariableFacadeHolderConfig _instance;

    @CapabilityInject(IVariableFacadeHolder.class)
    public static Capability<IVariableFacadeHolder> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public VariableFacadeHolderConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "variable_facade_holder",
            "Allows holding of variable facades.",
            IVariableFacadeHolder.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
