package ruiseki.integrateddynamics.recipe;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.RecipeConfig;
import ruiseki.okcore.recipe.type.crafting.SpecialRecipeSerializer;

/**
 * Config for {@link ItemFacadeRecipe}.
 * 
 * @author rubensworks
 */
public class ItemFacadeRecipeConfig extends RecipeConfig<ItemFacadeRecipe> {

    /**
     * The unique instance.
     */
    public static ItemFacadeRecipeConfig _instance;

    public ItemFacadeRecipeConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "crafting_special_facade",
            null,
            eConfig -> new SpecialRecipeSerializer<>(ItemFacadeRecipe::new));
    }

}
