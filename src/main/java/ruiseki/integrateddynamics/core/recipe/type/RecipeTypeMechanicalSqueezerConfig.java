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
public class RecipeTypeMechanicalSqueezerConfig extends RecipeTypeConfig<RecipeMechanicalSqueezer> {

    /**
     * The unique instance.
     */
    public static RecipeTypeMechanicalSqueezerConfig _instance;

    public RecipeTypeMechanicalSqueezerConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "mechanical_squeezer",
            null,
            config -> new IRecipeType<RecipeMechanicalSqueezer>() {});
    }

}
