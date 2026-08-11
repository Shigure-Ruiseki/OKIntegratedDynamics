package ruiseki.integrateddynamics.core.network;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import com.google.common.collect.Sets;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.api.ingredient.IIngredientPositionsIndex;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PrioritizedPartPos;
import ruiseki.okcore.datastructure.DistinctIterator;
import ruiseki.okcore.ingredient.collection.IIngredientCollectionMutable;
import ruiseki.okcore.ingredient.collection.IIngredientMapMutable;
import ruiseki.okcore.ingredient.collection.IngredientCollectionMutableWrapper;
import ruiseki.okcore.ingredient.collection.IngredientCollectionPrototypeMap;
import ruiseki.okcore.ingredient.collection.IngredientHashMap;

/**
 * An index that maps ingredients to positions that contain that instance.
 *
 * @param <T> An instance type.
 * @param <M> The matching condition parameter.
 * @author rubensworks
 */
public class IngredientPositionsIndex<T, M>
    extends IngredientCollectionMutableWrapper<T, M, IIngredientCollectionMutable<T, M>>
    implements IIngredientPositionsIndex<T, M> {

    private final IIngredientMapMutable<T, M, TreeSet<PrioritizedPartPos>> positionsMap;

    public IngredientPositionsIndex(IngredientComponent<T, M> component) {
        super(new IngredientCollectionPrototypeMap<>(component, false));
        this.positionsMap = new IngredientHashMap<>(component);
    }

    protected T getPrototype(T instance) {
        return this.positionsMap.getComponent()
            .getMatcher()
            .withQuantity(instance, 1);
    }

    @Override
    public Iterator<PartPos> getNonEmptyPositions() {
        return getPositions(
            getComponent().getMatcher()
                .getEmptyInstance(),
            getComponent().getMatcher()
                .getAnyMatchCondition());
    }

    @Override
    public Iterator<PartPos> getPositions(T instance, M matchFlags) {
        return new DistinctIterator<>(
            this.positionsMap.getAll(getPrototype(instance), matchFlags)
                .stream()
                .flatMap(Collection::stream)
                .map(PrioritizedPartPos::getPartPos)
                .iterator());
    }

    @Override
    public void addPosition(T instance, PrioritizedPartPos pos) {
        T prototype = getPrototype(instance);
        TreeSet<PrioritizedPartPos> set = this.positionsMap.get(prototype);
        if (set == null) {
            set = Sets.newTreeSet();
            this.positionsMap.put(prototype, set);
        }
        set.add(pos);
    }

    @Override
    public void removePosition(T instance, PrioritizedPartPos pos) {
        T prototype = getPrototype(instance);
        TreeSet<PrioritizedPartPos> set = this.positionsMap.get(prototype);
        if (set != null) {
            set.remove(pos);
            if (set.isEmpty()) {
                this.positionsMap.remove(prototype);
            }
        }
    }

}
