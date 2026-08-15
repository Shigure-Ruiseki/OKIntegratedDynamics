package ruiseki.integrateddynamics.capability.valueinterface;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integrateddynamics.api.evaluate.IValueInterface;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the value interface capability.
 * 
 * @author rubensworks
 *
 */
public class ValueInterfaceConfig extends CapabilityConfig<IValueInterface> {

    /**
     * The unique instance.
     */
    public static ValueInterfaceConfig _instance;

    @CapabilityInject(IValueInterface.class)
    public static Capability<IValueInterface> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public ValueInterfaceConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "value_interface_provider",
            "Capability for elements used for path construction",
            IValueInterface.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
