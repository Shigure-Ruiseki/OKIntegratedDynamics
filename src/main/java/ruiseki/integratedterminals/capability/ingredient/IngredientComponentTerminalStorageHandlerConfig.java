package ruiseki.integratedterminals.capability.ingredient;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.integratedterminals.api.ingredient.IIngredientComponentTerminalStorageHandler;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the ingredient component view capability.
 * 
 * @author rubensworks
 *
 */
public class IngredientComponentTerminalStorageHandlerConfig
    extends CapabilityConfig<IIngredientComponentTerminalStorageHandler> {

    /**
     * The unique instance.
     */
    public static IngredientComponentTerminalStorageHandlerConfig _instance;

    @CapabilityInject(IIngredientComponentTerminalStorageHandler.class)
    public static Capability<IIngredientComponentTerminalStorageHandler> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public IngredientComponentTerminalStorageHandlerConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "ingredientComponentTerminalStorageHandler",
            "Capability for displaying ingredient components of a certain type",
            IIngredientComponentTerminalStorageHandler.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
