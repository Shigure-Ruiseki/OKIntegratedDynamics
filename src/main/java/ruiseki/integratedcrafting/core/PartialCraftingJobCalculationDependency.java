package ruiseki.integratedcrafting.core;

import java.util.Collection;
import java.util.List;

import ruiseki.integratedcrafting.api.crafting.CraftingJob;
import ruiseki.integratedcrafting.api.crafting.UnknownCraftingRecipeException;

/**
 * @author rubensworks
 */
public class PartialCraftingJobCalculationDependency {

    private final List<UnknownCraftingRecipeException> unknownCrafingRecipes;
    private final Collection<CraftingJob> partialCraftingJobs;

    public PartialCraftingJobCalculationDependency(List<UnknownCraftingRecipeException> unknownCrafingRecipes,
        Collection<CraftingJob> partialCraftingJobs) {
        this.unknownCrafingRecipes = unknownCrafingRecipes;
        this.partialCraftingJobs = partialCraftingJobs;
    }

    public List<UnknownCraftingRecipeException> getUnknownCrafingRecipes() {
        return unknownCrafingRecipes;
    }

    public Collection<CraftingJob> getPartialCraftingJobs() {
        return partialCraftingJobs;
    }

    public boolean isValid() {
        return getUnknownCrafingRecipes().isEmpty();
    }
}
