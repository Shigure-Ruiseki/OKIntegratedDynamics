package ruiseki.integrateddynamics.core.network;

import java.util.Iterator;

import javax.annotation.Nonnull;

import ruiseki.integrateddynamics.api.ingredient.IIngredientPositionsIndex;
import ruiseki.integrateddynamics.api.part.PartPos;

/**
 * An ingredient channel that exploits the network's index.
 *
 * @param <T> The instance type.
 * @param <M> The matching condition parameter.
 */
public class IngredientChannelIndexed<T, M> extends IngredientChannelAdapter<T, M> {

    private final IIngredientPositionsIndex<T, M> index;

    public IngredientChannelIndexed(PositionedAddonsNetworkIngredients<T, M> network, int channel,
        IIngredientPositionsIndex<T, M> index) {
        super(network, channel);
        this.index = index;
    }

    @Override
    protected Iterator<PartPos> getNonFullPositions() {
        return this.getNetwork()
            .getPositions(getChannel())
            .iterator();
    }

    @Override
    protected Iterator<PartPos> getAllPositions() {
        return this.getNetwork()
            .getPositions(getChannel())
            .iterator();
    }

    @Override
    protected Iterator<PartPos> getNonEmptyPositions() {
        return this.index.getNonEmptyPositions();
    }

    @Override
    protected Iterator<PartPos> getMatchingPositions(@Nonnull T prototype, M matchFlags) {
        return this.index.getPositions(prototype, matchFlags);
    }

    @Override
    public Iterator<T> iterator() {
        return this.index.iterator();
    }

    @Override
    public Iterator<T> iterator(@Nonnull T prototype, M matchFlags) {
        return this.index.iterator(prototype, matchFlags);
    }
}
