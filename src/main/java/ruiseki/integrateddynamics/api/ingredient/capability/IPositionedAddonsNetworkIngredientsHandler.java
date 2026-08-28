package ruiseki.integrateddynamics.api.ingredient.capability;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import ruiseki.okcore.datastructure.LazyOptional;

/**
 * A capability that retrieves the {@link IPositionedAddonsNetworkIngredients}
 * of an {@link ruiseki.commoncapabilities.api.ingredient.IngredientComponent} in a network.
 *
 * @param <T> The instance type.
 * @param <M> The matching condition parameter, may be Void. Instances MUST properly implement the equals method.
 * @author rubensworks
 */
public interface IPositionedAddonsNetworkIngredientsHandler<T, M> {

    /**
     * Get the ingredient network in the given network,
     * 
     * @param network The network.
     * @return The optional ingredient component network.
     */
    public LazyOptional<IPositionedAddonsNetworkIngredients<T, M>> getStorage(INetwork network);

}
