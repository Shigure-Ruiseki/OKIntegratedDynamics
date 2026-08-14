package ruiseki.integratedterminals.modcompat.integratedcrafting;

import com.google.common.collect.Lists;
import ruiseki.commoncapabilities.api.ingredient.IMixedIngredients;
import ruiseki.commoncapabilities.api.ingredient.IPrototypedIngredient;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.api.ingredient.PrototypedIngredient;

import java.util.List;

/**
 * @author rubensworks
 */
public class IntegratedCraftingHelpers {

    public static List<IPrototypedIngredient<?, ?>> getPrototypesFromIngredients(IMixedIngredients ingredients) {
        List<IPrototypedIngredient<?, ?>> outputs = Lists.newArrayList();
        for (IngredientComponent<?, ?> component : ingredients.getComponents()) {
            for (Object instance : ingredients.getInstances(component)) {
                outputs.add(new PrototypedIngredient(component, instance, component.getMatcher().getExactMatchCondition()));
            }
        }
        return outputs;
    }

}
