package ruiseki.integratedcrafting.core;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedcrafting.api.crafting.CraftingJob;
import ruiseki.integratedcrafting.api.crafting.UnknownCraftingRecipeException;

/**
 * @author rubensworks
 */
public class PartialCraftingJobCalculation {

    @Nullable
    private final CraftingJob craftingJob;
    @Nullable
    private final List<UnknownCraftingRecipeException> missingDependencies;
    @Nullable
    private final Map<IngredientComponent<?, ?>, List<?>> ingredientsStorage;
    @Nullable
    private final List<CraftingJob> partialCraftingJobs;

    public PartialCraftingJobCalculation(@Nullable CraftingJob craftingJob,
        List<UnknownCraftingRecipeException> missingDependencies,
        Map<IngredientComponent<?, ?>, List<?>> ingredientsStorage, List<CraftingJob> partialCraftingJobs) {
        this.craftingJob = craftingJob;
        this.missingDependencies = missingDependencies;
        this.ingredientsStorage = ingredientsStorage;
        this.partialCraftingJobs = partialCraftingJobs;
    }

    @Nullable
    public CraftingJob getCraftingJob() {
        return craftingJob;
    }

    @Nullable
    public List<UnknownCraftingRecipeException> getMissingDependencies() {
        return missingDependencies;
    }

    @Nullable
    public Map<IngredientComponent<?, ?>, List<?>> getIngredientsStorage() {
        return ingredientsStorage;
    }

    @Nullable
    public List<CraftingJob> getPartialCraftingJobs() {
        return partialCraftingJobs;
    }
}
