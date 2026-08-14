package ruiseki.integratedcrafting.api.recipe;

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nullable;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedcrafting.api.crafting.CraftingJob;

/**
 * Indexes crafting jobs by expected output.
 * 
 * @author rubensworks
 */
public interface ICraftingJobIndex {

    /**
     * @return All crafting jobs that are available.
     */
    public Collection<CraftingJob> getCraftingJobs();

    /**
     * Find crafting jobs with the given output.
     * 
     * @param outputType     The recipe component type.
     * @param output         An output ingredient instance.
     * @param matchCondition A condition under which the matching should be done.
     * @param <T>            The instance type.
     * @param <M>            The matching condition parameter, may be Void.
     * @return An iterator of the crafting jobs that have the given output.
     */
    public <T, M> Iterator<CraftingJob> getCraftingJobs(IngredientComponent<T, M> outputType, T output,
        M matchCondition);

    /**
     * Get the crafting job with the given id.
     * 
     * @param craftingJobId A crafting job id.
     * @return A crafting job or null.
     */
    @Nullable
    public CraftingJob getCraftingJob(int craftingJobId);

}
