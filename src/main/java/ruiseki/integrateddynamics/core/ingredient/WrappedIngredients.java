package ruiseki.integrateddynamics.core.ingredient;

import java.util.List;
import java.util.Set;

import ruiseki.commoncapabilities.api.ingredient.IMixedIngredients;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.MixedIngredientsAdapter;

/**
 * A wrapper around ingredients.
 *
 * @author rubensworks
 */
public class WrappedIngredients extends MixedIngredientsAdapter {

    private final IMixedIngredients ingredients;

    public WrappedIngredients(IMixedIngredients ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public Set<IngredientComponent<?, ?>> getComponents() {
        return ingredients.getComponents();
    }

    @Override
    public <T> List<T> getInstances(IngredientComponent<T, ?> ingredientComponent) {
        return ingredients.getInstances(ingredientComponent);
    }

    @Override
    public int compareTo(IMixedIngredients o) {
        return ingredients.compareTo(o);
    }
}
