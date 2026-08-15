package ruiseki.integrateddynamics.core.evaluate.operator;

import java.util.concurrent.TimeUnit;

import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeDefinition;
import ruiseki.commoncapabilities.api.ingredient.IMixedIngredients;
import ruiseki.commoncapabilities.api.ingredient.MixedIngredients;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeIngredients;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueObjectTypeRecipe;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.okcore.datastructure.DimPos;

/**
 * An operator that gets the first recipes based on an input.
 *
 * @author rubensworks
 */
public class PositionedOperatorRecipeHandlerRecipeByInput<T extends IValueType<V>, V extends IValue>
    extends PositionedOperatorRecipeHandler<T, V> {

    private static final Cache<Pair<Pair<DimPos, ForgeDirection>, ValueObjectTypeIngredients.ValueIngredients>, ValueObjectTypeRecipe.ValueRecipe> CACHE = CacheBuilder
        .newBuilder()
        .expireAfterAccess(20, TimeUnit.SECONDS)
        .build();

    public PositionedOperatorRecipeHandlerRecipeByInput(DimPos pos, ForgeDirection side) {
        super("recipebyinput", new Function(), ValueTypes.OBJECT_RECIPE, pos, side);
    }

    public PositionedOperatorRecipeHandlerRecipeByInput() {
        this(null, null);
    }

    public static class Function extends PositionedOperatorRecipeHandlerRecipeByOutput.Function {

        protected boolean validateIngredients(IMixedIngredients actualIngredients, IMixedIngredients givenIngredients) {
            return validateIngredientsExact(actualIngredients, givenIngredients);
        }

        @Override
        protected Cache<Pair<Pair<DimPos, ForgeDirection>, ValueObjectTypeIngredients.ValueIngredients>, ValueObjectTypeRecipe.ValueRecipe> getCache() {
            return CACHE;
        }

        @Override
        protected IMixedIngredients getRecipeIngredients(IRecipeDefinition recipeDefinition) {
            return MixedIngredients.fromRecipeInput(recipeDefinition);
        }
    }

}
