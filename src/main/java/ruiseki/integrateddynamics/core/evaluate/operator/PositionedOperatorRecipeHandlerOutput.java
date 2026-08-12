package ruiseki.integrateddynamics.core.evaluate.operator;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeHandler;
import ruiseki.commoncapabilities.api.ingredient.IMixedIngredients;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeIngredients;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.okcore.datastructure.DimPos;

/**
 * An operator that gets the output of a recipe based on an input.
 *
 * @author rubensworks
 */
public class PositionedOperatorRecipeHandlerOutput<T extends IValueType<V>, V extends IValue>
    extends PositionedOperatorRecipeHandler<T, V> {

    private static final Cache<Pair<Pair<DimPos, ForgeDirection>, ValueObjectTypeIngredients.ValueIngredients>, ValueObjectTypeIngredients.ValueIngredients> CACHE = CacheBuilder
        .newBuilder()
        .expireAfterAccess(20, TimeUnit.SECONDS)
        .build();

    public PositionedOperatorRecipeHandlerOutput(DimPos pos, ForgeDirection side) {
        super("recipeoutputbyinput", new Function(), pos, side);
    }

    public PositionedOperatorRecipeHandlerOutput() {
        this(null, null);
    }

    public static class Function extends PositionedOperatorRecipeHandler.Function {

        @Override
        public IValue evaluate(SafeVariablesGetter variables) throws EvaluationException {
            ValueObjectTypeIngredients.ValueIngredients ingredients = variables
                .getValue(0, ValueTypes.OBJECT_INGREDIENTS);
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
                        IMixedIngredients output = recipeHandler.simulate(
                            ingredients.getRawValue()
                                .get());
                        return ValueObjectTypeIngredients.ValueIngredients.of(output);
                    });
                } catch (ExecutionException e) {

                }
            }
            return ValueObjectTypeIngredients.ValueIngredients.of(null);
        }
    }

}
