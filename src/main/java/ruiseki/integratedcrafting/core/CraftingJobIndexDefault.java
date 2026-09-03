package ruiseki.integratedcrafting.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedcrafting.api.crafting.CraftingJob;
import ruiseki.integratedcrafting.api.recipe.ICraftingJobIndexModifiable;
import ruiseki.okcore.datastructure.MultitransformIterator;
import ruiseki.okcore.ingredient.collection.IIngredientMapMutable;
import ruiseki.okcore.ingredient.collection.IngredientHashMap;
import ruiseki.okcore.ingredient.collection.IngredientMapSingleClassified;

/**
 * A default implementation of {@link ruiseki.integratedcrafting.api.recipe.ICraftingJobIndex} and
 * {@link ICraftingJobIndexModifiable}.
 *
 * @author rubensworks
 */
public class CraftingJobIndexDefault implements ICraftingJobIndexModifiable {

    private final Map<IngredientComponent<?, ?>, IIngredientMapMutable<?, ?, Collection<CraftingJob>>> recipeComponentIndexes;
    private final Int2ObjectMap<CraftingJob> craftingJobs;

    public CraftingJobIndexDefault() {
        this.recipeComponentIndexes = Maps.newIdentityHashMap();
        this.craftingJobs = new Int2ObjectOpenHashMap<>();
    }

    @Override
    public Collection<CraftingJob> getCraftingJobs() {
        return Collections.unmodifiableCollection(craftingJobs.values());
    }

    @Override
    public <T, M> Iterator<CraftingJob> getCraftingJobs(IngredientComponent<T, M> outputType, T output,
        M matchCondition) {
        IIngredientMapMutable<?, ?, Collection<CraftingJob>> index = recipeComponentIndexes.get(outputType);
        if (index == null) {
            return Iterators.forArray();
        }
        return MultitransformIterator.flattenIterableIterator(
            Iterators.transform(
                ((IIngredientMapMutable<T, M, Collection<CraftingJob>>) index).iterator(output, matchCondition),
                (entry) -> entry.getValue()));
    }

    @Nullable
    @Override
    public CraftingJob getCraftingJob(int craftingJobId) {
        return craftingJobs.get(craftingJobId);
    }

    @Nullable
    protected <T, M> IIngredientMapMutable<T, M, Collection<CraftingJob>> initializeIndex(
        IngredientComponent<T, M> recipeComponent) {
        // Classify by the component's primary category, just like RecipeIndexDefault does.
        // Lookups in this index are done with quantity-less match conditions,
        // which a plain hash map can only answer by filtering over every indexed crafting job.
        if (recipeComponent.getCategoryTypes()
            .size() == 1) {
            return new IngredientHashMap<>(recipeComponent);
        }
        return new IngredientMapSingleClassified<>(
            recipeComponent,
            () -> new IngredientHashMap<>(recipeComponent),
            recipeComponent.getCategoryTypes()
                .get(0));
    }

    @Override
    public void addCraftingJob(CraftingJob craftingJob) {
        craftingJobs.put(craftingJob.getId(), craftingJob);
        for (IngredientComponent<?, ?> recipeComponent : craftingJob.getRecipe()
            .getOutput()
            .getComponents()) {
            IIngredientMapMutable<?, ?, Collection<CraftingJob>> index = recipeComponentIndexes
                .computeIfAbsent(recipeComponent, this::initializeIndex);
            if (index != null) {
                addCraftingJobForComponent(index, craftingJob);
            }
        }
    }

    protected <T, M> void addCraftingJobForComponent(IIngredientMapMutable<T, M, Collection<CraftingJob>> index,
        CraftingJob craftingJob) {
        for (T instance : craftingJob.getRecipe()
            .getOutput()
            .getInstances(index.getComponent())) {
            Collection<CraftingJob> set = index.get(instance);
            if (set == null) {
                set = Sets.newIdentityHashSet();
                index.put(instance, set);
            }
            set.add(craftingJob);
        }
    }

    @Override
    public void removeCraftingJob(CraftingJob craftingJob) {
        craftingJobs.remove(craftingJob.getId());
        for (IngredientComponent<?, ?> recipeComponent : craftingJob.getRecipe()
            .getOutput()
            .getComponents()) {
            IIngredientMapMutable<?, ?, Collection<CraftingJob>> index = recipeComponentIndexes.get(recipeComponent);
            if (index != null) {
                removeCraftingJobForComponent(index, craftingJob);
            }
        }
    }

    protected <T, M> void removeCraftingJobForComponent(IIngredientMapMutable<T, M, Collection<CraftingJob>> index,
        CraftingJob craftingJob) {
        for (T instance : craftingJob.getRecipe()
            .getOutput()
            .getInstances(index.getComponent())) {
            Collection<CraftingJob> set = index.get(instance);
            if (set != null) {
                if (set.remove(craftingJob)) {
                    if (set.isEmpty()) {
                        index.remove(instance);
                    }
                }
            }
        }
    }

}
