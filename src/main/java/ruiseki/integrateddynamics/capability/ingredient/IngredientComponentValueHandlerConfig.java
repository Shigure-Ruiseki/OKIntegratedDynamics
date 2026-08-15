package ruiseki.integrateddynamics.capability.ingredient;

import ruiseki.commoncapabilities.CommonCapabilities;
import ruiseki.okcore.capabilities.Capability;
import ruiseki.okcore.capabilities.CapabilityInject;
import ruiseki.okcore.config.extendedconfig.CapabilityConfig;

/**
 * Config for the ingredient component value handler capability.
 * 
 * @author rubensworks
 *
 */
public class IngredientComponentValueHandlerConfig extends CapabilityConfig<IIngredientComponentValueHandler> {

    /**
     * The unique instance.
     */
    public static IngredientComponentValueHandlerConfig _instance;

    @CapabilityInject(IIngredientComponentValueHandler.class)
    public static Capability<IIngredientComponentValueHandler> CAPABILITY = null;

    /**
     * Make a new instance.
     */
    public IngredientComponentValueHandlerConfig() {
        super(
            CommonCapabilities._instance,
            true,
            "ingredientComponentHandler",
            "Handles the translation between IngredientComponent instances and IValue",
            IIngredientComponentValueHandler.class);
    }

    @Override
    public boolean isDisableable() {
        return false;
    }

}
