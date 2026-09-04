package ruiseki.integrateddynamics.api.ingredient.capability;

import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPositionedAddonsNetworkIngredients;
import ruiseki.okcore.datastructure.LazyOptional;

/**
 * Default implementation of {@link IPositionedAddonsNetworkIngredientsHandler}.
 *
 * @author rubensworks
 */
public class DefaultPositionedAddonsNetworkIngredientsHandler<T, M>
    implements IPositionedAddonsNetworkIngredientsHandler<T, M> {

    private final Function<INetwork, IPositionedAddonsNetworkIngredients<T, M>> networkRetriever;

    public DefaultPositionedAddonsNetworkIngredientsHandler(
        Function<INetwork, IPositionedAddonsNetworkIngredients<T, M>> networkRetriever) {
        this.networkRetriever = networkRetriever;
    }

    @Nullable
    @Override
    public LazyOptional<IPositionedAddonsNetworkIngredients<T, M>> getStorage(INetwork network) {
        return LazyOptional.of(() -> networkRetriever.apply(network));
    }
}
