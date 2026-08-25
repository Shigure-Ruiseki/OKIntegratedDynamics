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
public class RecipeTypeSqueezerConfig extends RecipeTypeConfig<RecipeSqueezer> {

    /**
     * The unique instance.
     */
    public static RecipeTypeSqueezerConfig _instance;

    public RecipeTypeSqueezerConfig() {
        super(IntegratedDynamics._instance, true, "squeezer", null, config -> new IRecipeType<RecipeSqueezer>() {});
    }

}
