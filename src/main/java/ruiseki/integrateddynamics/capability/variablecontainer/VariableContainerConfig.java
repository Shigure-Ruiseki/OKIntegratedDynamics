package ruiseki.integrateddynamics.capability.variablecontainer;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integrateddynamics.api.block.IVariableContainer;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the variable container capability.
 *
 * @author rubensworks
 *
 */
public class VariableContainerConfig extends CapabilityConfig<IVariableContainer> {

    /**
     * The unique instance.
     */
    public static VariableContainerConfig _instance;

    @CapabilityInject(IVariableContainer.class)
    public static Capability<IVariableContainer> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public VariableContainerConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "variableContainer",
            "Allows storage of variables.",
            IVariableContainer.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
