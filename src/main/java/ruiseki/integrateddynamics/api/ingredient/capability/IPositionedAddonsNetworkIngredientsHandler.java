package ruiseki.integrateddynamics.api.ingredient.capability;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;

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
     * @return The ingredient component network, or null if it is not available.
     */
    @Nullable
    public IPositionedAddonsNetworkIngredients<T, M> getStorage(INetwork network);

}
