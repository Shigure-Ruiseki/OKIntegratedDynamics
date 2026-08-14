package ruiseki.integratedcrafting.core;

import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import ruiseki.commoncapabilities.api.ingredient.IIngredientMatcher;
import ruiseki.commoncapabilities.api.ingredient.IPrototypedIngredient;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.PrototypedIngredient;
import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.integratedcrafting.api.crafting.CraftingJob;
import ruiseki.integrateddynamics.api.ingredient.IIngredientComponentStorageObservable;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetwork;
import ruiseki.okcore.ingredient.collection.IIngredientCollection;
import ruiseki.okcore.ingredient.collection.IngredientCollectionPrototypeMap;
import ruiseki.okcore.ingredient.storage.IngredientComponentStorageCollectionWrapper;

/**
 * An ingredient index observer that tracks crafting job outputs for a certain ingredient component type.
 *
 * It will observe changes and (partially) resolve awaiting crafting job outputs when applicable.
 *
 * @author rubensworks
 */
public class PendingCraftingJobResultIndexObserver<T, M>
    implements IIngredientComponentStorageObservable.IIndexChangeObserver<T, M> {

    private final IngredientComponent<T, M> ingredientComponent;
    private final CraftingJobHandler handler;

    public PendingCraftingJobResultIndexObserver(IngredientComponent<T, M> ingredientComponent,
        CraftingJobHandler handler) {
        this.ingredientComponent = ingredientComponent;
        this.handler = handler;
    }

    @Override
    public void onChange(IIngredientComponentStorageObservable.StorageChangeEvent<T, M> event) {
        if (event.getChangeType() == IIngredientComponentStorageObservable.Change.ADDITION) {
            IIngredientCollection<T, M> addedIngredients = event.getInstances();
            IIngredientComponentStorage<T, M> ingredientsHayStack = null; // A mutable copy of addedIngredients (lazily
                                                                          // created)
            IIngredientMatcher<T, M> matcher = ingredientComponent.getMatcher();

            Int2ObjectMap<Map<IngredientComponent<?, ?>, List<IPrototypedIngredient<?, ?>>>> processingJobs = handler
                .getProcessingCraftingJobsPendingIngredients();
            ObjectIterator<Int2ObjectMap.Entry<Map<IngredientComponent<?, ?>, List<IPrototypedIngredient<?, ?>>>>> jobEntryIt = processingJobs
                .int2ObjectEntrySet()
                .iterator();
            while (jobEntryIt.hasNext()) {
                Int2ObjectMap.Entry<Map<IngredientComponent<?, ?>, List<IPrototypedIngredient<?, ?>>>> jobEntry = jobEntryIt
                    .next();
                // Only check jobs that have a matching channel with the event
                CraftingJob craftingJob = handler.getAllCraftingJobs()
                    .get(jobEntry.getIntKey());
                if (craftingJob != null && (craftingJob.getChannel() == IPositionedAddonsNetwork.WILDCARD_CHANNEL
                    || craftingJob.getChannel() == event.getChannel())) {
                    List<IPrototypedIngredient<?, ?>> pendingIngredientsUnsafe = jobEntry.getValue()
                        .get(ingredientComponent);
                    if (pendingIngredientsUnsafe != null) {
                        // Remove pending ingredients that were added in the event
                        List<IPrototypedIngredient<T, M>> pendingIngredients = (List<IPrototypedIngredient<T, M>>) (Object) pendingIngredientsUnsafe;

                        // Iterate over all pending ingredients for this ingredient component
                        ListIterator<IPrototypedIngredient<T, M>> it = pendingIngredients.listIterator();
                        while (it.hasNext()) {
                            IPrototypedIngredient<T, M> prototypedIngredient = it.next();
                            final long initialQuantity = matcher.getQuantity(prototypedIngredient.getPrototype());
                            long remainingQuantity = initialQuantity;

                            // Lazily create ingredientsHayStack only when needed,
                            // because we need to copy all ingredients from addedIngredients,
                            // which can get expensive
                            // We need to make a copy because multiple crafting jobs can have the same pending
                            // instances,
                            // so each instance may only be consumed by a single crafting job.
                            if (ingredientsHayStack == null) {
                                if (addedIngredients.contains(
                                    prototypedIngredient.getPrototype(),
                                    prototypedIngredient.getCondition())) {
                                    IngredientCollectionPrototypeMap<T, M> prototypeMap = new IngredientCollectionPrototypeMap<>(
                                        ingredientComponent);
                                    ingredientsHayStack = new IngredientComponentStorageCollectionWrapper<>(
                                        prototypeMap);
                                    prototypeMap.addAll(addedIngredients);
                                } else {
                                    continue;
                                }
                            }

                            // Iteratively extract the pending ingredient from the hay stack.
                            T extracted;
                            do {
                                extracted = ingredientsHayStack.extract(
                                    prototypedIngredient.getPrototype(),
                                    prototypedIngredient.getCondition(),
                                    false);

                                if (matcher.isEmpty(extracted)) {
                                    // Quickly break when no matches are available anymore
                                    break;
                                }

                                remainingQuantity -= matcher.getQuantity(extracted);
                            } while (!matcher.isEmpty(extracted) && remainingQuantity > 0);

                            // Update the list if the prototype has changed.
                            if (remainingQuantity <= 0) {
                                it.remove();
                            } else if (initialQuantity != remainingQuantity) {
                                it.set(
                                    new PrototypedIngredient<>(
                                        ingredientComponent,
                                        matcher.withQuantity(prototypedIngredient.getPrototype(), remainingQuantity),
                                        prototypedIngredient.getCondition()));
                            }
                        }

                        // If no prototypes for this component type for this crafting job are pending.
                        if (pendingIngredients.isEmpty()) {
                            // Remove observer (in next tick) when all pending ingredients are resolved
                            handler.getObserversPendingDeletion()
                                .add(ingredientComponent);

                            // Remove crafting job if needed.
                            jobEntry.getValue()
                                .remove(ingredientComponent);
                            if (jobEntry.getValue()
                                .isEmpty()) {
                                handler.onCraftingJobFinished(
                                    handler.getAllCraftingJobs()
                                        .get(jobEntry.getIntKey()));
                                handler.getProcessingCraftingJobsRaw()
                                    .remove(jobEntry.getIntKey());
                                jobEntryIt.remove();
                            }
                        }
                    }
                }
            }
        }
    }

}
