package ruiseki.integrateddynamics.core.evaluate.operator;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeDefinition;
import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeHandler;
import ruiseki.commoncapabilities.api.ingredient.IMixedIngredients;
import ruiseki.commoncapabilities.api.ingredient.MixedIngredients;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeIngredients;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeList;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.okcore.datastructure.DimPos;

/**
 * An operator that gets the input of a recipe based on an output.
 * 
 * @author rubensworks
 */
public class PositionedOperatorRecipeHandlerInputs<T extends IValueType<V>, V extends IValue>
    extends PositionedOperatorRecipeHandler<T, V> {

    private static final Cache<Pair<Pair<DimPos, ForgeDirection>, ValueObjectTypeIngredients.ValueIngredients>, ValueTypeList.ValueList> CACHE = CacheBuilder
        .newBuilder()
        .expireAfterAccess(20, TimeUnit.SECONDS)
        .build();

    public PositionedOperatorRecipeHandlerInputs(DimPos pos, ForgeDirection side) {
        super("recipeinputsbyoutput", new Function(), ValueTypes.LIST, pos, side);
    }

    public PositionedOperatorRecipeHandlerInputs() {
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
                    return CACHE.get(key, () -> {
                        IMixedIngredients givenIngredients = ingredients.getRawValue()
                            .get();
                        List<ValueObjectTypeIngredients.ValueIngredients> validIngredients = Lists.newArrayList();
                        for (IRecipeDefinition recipe : recipeHandler.getRecipes()) {
                            IMixedIngredients outputIngredients = recipe.getOutput();
                            // If one valid recipe is found, add to list
                            if (validateIngredientsPartial(outputIngredients, givenIngredients)) {
                                validIngredients.add(
                                    ValueObjectTypeIngredients.ValueIngredients
                                        .of(MixedIngredients.fromRecipeInput(recipe)));
                            }
                        }
                        return ValueTypeList.ValueList.ofList(ValueTypes.OBJECT_INGREDIENTS, validIngredients);
                    });
                } catch (ExecutionException e) {

                }
            }
            return ValueTypeList.ValueList.ofList(ValueTypes.OBJECT_INGREDIENTS, Collections.emptyList());
        }
    }

}
