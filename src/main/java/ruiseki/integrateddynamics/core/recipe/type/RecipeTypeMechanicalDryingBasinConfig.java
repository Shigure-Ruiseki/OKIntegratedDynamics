package ruiseki.integrateddynamics.core.recipe.type;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.RecipeTypeConfig;
import ruiseki.okcore.recipe.IRecipeType;

/**
 * Config for the mechanical drying basin recipe type.
 *
 * @author rubensworks
 *
 */
public class RecipeTypeMechanicalDryingBasinConfig extends RecipeTypeConfig<RecipeMechanicalDryingBasin> {

    /**
     * The unique instance.
     */
    public static RecipeTypeMechanicalDryingBasinConfig _instance;

    public RecipeTypeMechanicalDryingBasinConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "mechanical_drying_basin",
            null,
            config -> new IRecipeType<RecipeMechanicalDryingBasin>() {});
    }

}
