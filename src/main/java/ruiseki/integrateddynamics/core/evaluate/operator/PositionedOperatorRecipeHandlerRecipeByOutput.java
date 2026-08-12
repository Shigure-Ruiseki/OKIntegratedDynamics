package ruiseki.integrateddynamics.core.evaluate.operator;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeDefinition;
import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeHandler;
import ruiseki.commoncapabilities.api.ingredient.IMixedIngredients;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeIngredients;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeRecipe;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeList;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.okcore.datastructure.DimPos;

/**
 * An operator that gets the first recipe based on an output.
 * 
 * @author rubensworks
 */
public class PositionedOperatorRecipeHandlerRecipeByOutput<T extends IValueType<V>, V extends IValue>
    extends PositionedOperatorRecipeHandler<T, V> {

    private static final Cache<Pair<Pair<DimPos, ForgeDirection>, ValueObjectTypeIngredients.ValueIngredients>, ValueObjectTypeRecipe.ValueRecipe> CACHE = CacheBuilder
        .newBuilder()
        .expireAfterAccess(20, TimeUnit.SECONDS)
        .build();

    public PositionedOperatorRecipeHandlerRecipeByOutput(DimPos pos, ForgeDirection side) {
        super("recipebyoutput", new Function(), ValueTypes.OBJECT_RECIPE, pos, side);
    }

    public PositionedOperatorRecipeHandlerRecipeByOutput() {
        this(null, null);
    }

    public static class Function extends PositionedOperatorRecipeHandler.Function {

        @Override
        public IValue evaluate(SafeVariablesGetter variables) throws EvaluationException {
            ValueObjectTypeIngredients.ValueIngredients ingredients = variables.getValue(0);
            IRecipeHandler recipeHandler = this.getOperator()
                .getRecipeHandler();
            if (recipeHandler != null && ingredients.getRawValue()
                .isPresent()) {
                Pair<Pair<DimPos, ForgeDirection>, ValueObjectTypeIngredients.ValueIngredients> key = Pair.of(
                    Pair.of(
                        this.getOperator()
                            .getPos(),
                        this.getOperator()
                            .getSide()),
                    ingredients);
                try {
                    return getCache().get(key, () -> {
                        IMixedIngredients givenIngredients = ingredients.getRawValue()
                            .get();
                        for (IRecipeDefinition recipe : recipeHandler.getRecipes()) {
                            IMixedIngredients outputIngredients = getRecipeIngredients(recipe);
                            // If one valid recipe is found, return it
                            if (recipe.getInputComponents()
                                .size() > 0
                                && recipe.getOutput()
                                    .getComponents()
                                    .size() > 0
                                && validateIngredients(outputIngredients, givenIngredients)) {
                                return ValueObjectTypeRecipe.ValueRecipe.of(recipe);
                            }
                        }
                        return ValueObjectTypeRecipe.ValueRecipe.of(null);
                    });
                } catch (ExecutionException e) {

                }
            }
            return ValueTypeList.ValueList.ofList(ValueTypes.OBJECT_RECIPE, Collections.emptyList());
        }

        protected boolean validateIngredients(IMixedIngredients actualIngredients, IMixedIngredients givenIngredients) {
            return validateIngredientsPartial(actualIngredients, givenIngredients);
        }

        protected Cache<Pair<Pair<DimPos, ForgeDirection>, ValueObjectTypeIngredients.ValueIngredients>, ValueObjectTypeRecipe.ValueRecipe> getCache() {
            return CACHE;
        }

        protected IMixedIngredients getRecipeIngredients(IRecipeDefinition recipeDefinition) {
            return recipeDefinition.getOutput();
        }
    }

}
