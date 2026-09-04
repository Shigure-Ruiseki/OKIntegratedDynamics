package ruiseki.integrateddynamics.api.network;

import javax.annotation.Nonnull;

import ruiseki.commoncapabilities.api.ingredient.storage.IIngredientComponentStorage;
import ruiseki.integrateddynamics.api.part.PartPos;

/**
 * @author rubensworks
 */
public interface INetworkIngredientsChannel<T, M> extends IIngredientComponentStorage<T, M> {

    public Iterable<PartPos> findNonFullPositions();

    public Iterable<PartPos> findAllPositions();

    public Iterable<PartPos> findNonEmptyPositions();

    public Iterable<PartPos> findMatchingPositions(@Nonnull T prototype, M matchFlags);
}
