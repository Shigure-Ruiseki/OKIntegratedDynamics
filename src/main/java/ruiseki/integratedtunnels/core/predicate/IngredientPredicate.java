package ruiseki.integratedtunnels.core.predicate;

import java.util.Objects;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integratedtunnels.part.aspect.ITunnelTransfer;

/**
 * A predicate for matching ingredient components.
 * 
 * @param <T> The instance type.
 * @param <M> The matching condition parameter.
 * @author rubensworks
 */
public abstract class IngredientPredicate<T, M> implements Predicate<T>, ITunnelTransfer {

    private final IngredientComponent<T, M> ingredientComponent;
    @Nullable
    private final T instance;
    private final M matchFlags;
    private final boolean blacklist;
    private final boolean empty;
    private final int maxQuantity;
    private final boolean exactQuantity;

    public IngredientPredicate(IngredientComponent<T, M> ingredientComponent, @Nullable T instance, M matchFlags,
        boolean blacklist, boolean empty, int maxQuantity, boolean exactQuantity) {
        this.ingredientComponent = ingredientComponent;
        this.instance = instance;
        this.matchFlags = matchFlags;
        this.blacklist = blacklist;
        this.empty = empty;
        this.maxQuantity = maxQuantity;
        this.exactQuantity = exactQuantity;
    }

    // Note: implementors of this method *should* override equals and hashcode.
    public IngredientPredicate(IngredientComponent<T, M> ingredientComponent, boolean blacklist, boolean empty,
        int maxQuantity, boolean exactQuantity) {
        this(
            ingredientComponent,
            ingredientComponent.getMatcher()
                .getEmptyInstance(),
            null,
            blacklist,
            empty,
            maxQuantity,
            exactQuantity);
    }

    public IngredientComponent<T, M> getIngredientComponent() {
        return ingredientComponent;
    }

    @Nullable
    public T getInstance() {
        return instance;
    }

    public M getMatchFlags() {
        return matchFlags;
    }

    public boolean hasMatchFlags() {
        return matchFlags != null && !blacklist;
    }

    public boolean isBlacklist() {
        return blacklist;
    }

    public boolean isEmpty() {
        return empty;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public boolean isExactQuantity() {
        return exactQuantity;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IngredientPredicate that)) {
            return false;
        }
        return this.ingredientComponent == that.ingredientComponent && Objects.equals(this.instance, that.instance)
            && Objects.equals(this.matchFlags, that.matchFlags)
            && this.blacklist == that.blacklist
            && this.empty == that.empty
            && this.maxQuantity == that.maxQuantity
            && this.exactQuantity == that.exactQuantity;
    }

    @Override
    public int hashCode() {
        return ingredientComponent.hashCode() ^ (instance != null ? ingredientComponent.getMatcher()
            .hash(instance) : 0)
            ^ Objects.hashCode(matchFlags)
            ^ (blacklist ? 1 : 0)
            ^ (empty ? 2 : 4)
            ^ maxQuantity
            ^ (exactQuantity ? 8 : 16);
    }

    public static enum EmptyBehaviour {

        ANY,
        NONE;

        public static EmptyBehaviour fromBoolean(boolean emptyIsAny) {
            return emptyIsAny ? ANY : NONE;
        }
    }
}
