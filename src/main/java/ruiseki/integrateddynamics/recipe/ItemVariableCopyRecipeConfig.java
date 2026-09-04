package ruiseki.integrateddynamics.recipe;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.RecipeConfig;
import ruiseki.okcore.recipe.type.crafting.SpecialRecipeSerializer;

/**
 * Config for {@link ItemVariableCopyRecipe}.
 * 
 * @author rubensworks
 */
public class ItemVariableCopyRecipeConfig extends RecipeConfig<ItemVariableCopyRecipe> {

    /**
     * The unique instance.
     */
    public static ItemVariableCopyRecipeConfig _instance;

    public ItemVariableCopyRecipeConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "crafting_special_variable_copy",
            null,
            eConfig -> new SpecialRecipeSerializer<>(ItemVariableCopyRecipe::new));
    }

}
