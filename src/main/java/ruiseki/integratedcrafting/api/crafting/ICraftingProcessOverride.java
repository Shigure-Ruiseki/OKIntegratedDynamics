package ruiseki.integratedcrafting.api.crafting;

import java.util.function.Function;

import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeDefinition;
import ruiseki.commoncapabilities.api.ingredient.IMixedIngredients;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.api.part.PartPos;

/**
 * A certain override for performing a crafting process.
 *
 * @author rubensworks
 */
public interface ICraftingProcessOverride {

    /**
     * Check if this override applies to the given target.
     *
     * @param target A target position.
     * @return If this override is applicable.
     */
    public boolean isApplicable(PartPos target);

    /**
     * Start a crafting process with the given ingredients.
     *
     * @param targetGetter A function to get the target position.
     * @param ingredients  The ingredients to insert.
     * @param resultsSink  A sink where the ingredients can optionally be inserted into.
     *                     This should only be used if the processor does not have an internal storage.
     * @param craftingJob  The running crafting job (or pending job if simulating).
     * @param simulate     If insertion should be simulated.
     * @return If all instances could be inserted.
     */
    public boolean craft(Function<IngredientComponent<?, ?>, PartPos> targetGetter, IMixedIngredients ingredients,
        IRecipeDefinition recipe, ICraftingResultsSink resultsSink, CraftingJob craftingJob, boolean simulate);

}
