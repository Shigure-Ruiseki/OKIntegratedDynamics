package ruiseki.integrateddynamics.core.recipe.type;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.RecipeTypeConfig;
import ruiseki.okcore.recipe.IRecipeType;

/**
 * Config for the drying basin recipe type.
 * 
 * @author rubensworks
 *
 */
public class RecipeTypeDryingBasinConfig extends RecipeTypeConfig<RecipeDryingBasin> {

    /**
     * The unique instance.
     */
    public static RecipeTypeDryingBasinConfig _instance;

    public RecipeTypeDryingBasinConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "drying_basin",
            null,
            config -> new IRecipeType<RecipeDryingBasin>() {});
    }

}
