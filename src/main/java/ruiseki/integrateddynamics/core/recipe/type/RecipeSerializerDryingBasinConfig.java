package ruiseki.integrateddynamics.core.recipe.type;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.RecipeConfig;

/**
 * Config for the drying basin recipe serializer.
 * 
 * @author rubensworks
 *
 */
public class RecipeSerializerDryingBasinConfig extends RecipeConfig<RecipeDryingBasin> {

    /**
     * The unique instance.
     */
    public static RecipeSerializerDryingBasinConfig _instance;

    public RecipeSerializerDryingBasinConfig() {
        super(IntegratedDynamics._instance, true, "drying_basin", null, eConfig -> new RecipeSerializerDryingBasin());
    }

}
