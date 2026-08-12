package ruiseki.integrateddynamics.core.evaluate.operator;

import java.util.List;

import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeHandler;
import ruiseki.commoncapabilities.api.ingredient.IMixedIngredients;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.commoncapabilities.capability.recipehandler.RecipeHandlerConfig;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperator;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.Helpers;

/**
 * An operator related to a recipe handler.
 * 
 * @author rubensworks
 */
public class PositionedOperatorRecipeHandler<T extends IValueType<V>, V extends IValue> extends PositionedOperator {

    private final String unlocalizedType;

    public PositionedOperatorRecipeHandler(String name, Function function, IValueType output, DimPos pos,
        ForgeDirection side) {
        super(
            name,
            name,
            new IValueType[] { ValueTypes.OBJECT_INGREDIENTS },
            output,
            function,
            IConfigRenderPattern.PREFIX_1,
            pos,
            side);
        this.unlocalizedType = "virtual";
        ((Function) this.getFunction()).setOperator(this);
    }

    public PositionedOperatorRecipeHandler(String name, Function function, DimPos pos, ForgeDirection side) {
        super(
            name,
            name,
            new IValueType[] { ValueTypes.OBJECT_INGREDIENTS },
            ValueTypes.OBJECT_INGREDIENTS,
            function,
            IConfigRenderPattern.PREFIX_1,
            pos,
            side);
        this.unlocalizedType = "virtual";
        ((Function) this.getFunction()).setOperator(this);
    }

    @Nullable
    protected IRecipeHandler getRecipeHandler() {
        return Helpers
            .getTileOrBlockCapability(
                getPos().getWorld(),
                getPos().getBlockPos(),
                RecipeHandlerConfig.CAPABILITY,
                getSide())
            .getOrNull();
    }

    @Override
    protected String getUnlocalizedType() {
        return unlocalizedType;
    }

    @Override
    public IOperator materialize() {
        return this;
    }

    public static abstract class Function implements IFunction {

        private PositionedOperatorRecipeHandler operator;

        public void setOperator(PositionedOperatorRecipeHandler operator) {
            this.operator = operator;
        }

        public PositionedOperatorRecipeHandler getOperator() {
            return operator;
        }
    }

    public static boolean validateIngredientsExact(IMixedIngredients ingredients, IMixedIngredients givenIngredients) {
        for (IngredientComponent component : ingredients.getComponents()) {
            List<?> actualComponents = ingredients.getInstances(component);
            List<?> givenComponents = givenIngredients.getInstances(component);

            if (actualComponents.size() != givenComponents.size()) {
                return false;
            }

            // All components must be valid
            for (int i = 0; i < actualComponents.size(); i++) {
                Object actualIngredient = actualComponents.get(i);
                Object givenIngredient = givenComponents.get(i);
                if (!component.getMatcher()
                    .matchesExactly(givenIngredient, actualIngredient)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean validateIngredientsPartial(IMixedIngredients ingredients,
        IMixedIngredients givenIngredients) {
        for (IngredientComponent component : ingredients.getComponents()) {
            List<?> actualComponents = ingredients.getInstances(component);
            List<?> givenComponents = givenIngredients.getInstances(component);

            // At least all given components must match,
            // the actual component count may be larger.
            if (actualComponents.size() < givenComponents.size()) {
                return false;
            }

            // All GIVEN ingredients must match,
            // and all actual components may only be matched at most ONCE.
            List<Integer> actualIndexBlacklist = Lists.newLinkedList();
            for (Object givenIngredient : givenComponents) {
                boolean match = false;
                for (int i = 0; i < actualComponents.size(); i++) {
                    if (!actualIndexBlacklist.contains(i) && component.getMatcher()
                        .matchesExactly(givenIngredient, actualComponents.get(i))) {
                        match = true;
                        break;
                    }
                }
                if (!match) {
                    return false;
                }
            }
        }
        return true;
    }

}
