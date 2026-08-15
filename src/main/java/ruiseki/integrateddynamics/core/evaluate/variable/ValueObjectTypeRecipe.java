package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import com.google.common.collect.Iterables;

import joptsimple.internal.Strings;
import lombok.ToString;
import ruiseki.commoncapabilities.api.capability.recipehandler.IPrototypedIngredientAlternatives;
import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeDefinition;
import ruiseki.commoncapabilities.api.ingredient.IPrototypedIngredient;
import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNamed;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNullable;
import ruiseki.integrateddynamics.api.ingredient.IIngredientComponentHandler;
import ruiseki.integrateddynamics.core.ingredient.IngredientComponentHandlers;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeLPElementBase;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeRecipeLPElement;

/**
 * Value type with values that are recipes.
 *
 * @author rubensworks
 */
public class ValueObjectTypeRecipe extends ValueObjectTypeBase<ValueObjectTypeRecipe.ValueRecipe> implements
    IValueTypeNamed<ValueObjectTypeRecipe.ValueRecipe>, IValueTypeNullable<ValueObjectTypeRecipe.ValueRecipe> {

    public ValueObjectTypeRecipe() {
        super("recipe", ValueObjectTypeRecipe.ValueRecipe.class);
    }

    @Override
    public ValueRecipe getDefault() {
        return ValueRecipe.of(null);
    }

    @Override
    public String toCompactString(ValueRecipe value) {
        if (value.getRawValue()
            .isPresent()) {
            IRecipeDefinition recipe = value.getRawValue()
                .get();
            StringBuilder sb = new StringBuilder();

            sb.append(ValueObjectTypeIngredients.ingredientsToString(recipe.getOutput()));
            sb.append(" <- ");

            for (IngredientComponent<?, ?> component : recipe.getInputComponents()) {
                IIngredientComponentHandler handler = IngredientComponentHandlers.REGISTRY
                    .getComponentHandler(component);
                for (IPrototypedIngredientAlternatives<?, ?> instances : recipe.getInputs(component)) {
                    IPrototypedIngredient<?, ?> prototypedIngredient = Iterables
                        .getFirst(instances.getAlternatives(), null);
                    IValue v;
                    if (prototypedIngredient == null) {
                        v = handler.getValueType()
                            .getDefault();
                    } else {
                        v = handler.toValue(prototypedIngredient.getPrototype());
                    }
                    sb.append(handler.toCompactString(v));
                    sb.append(", ");
                }
            }
            String str = sb.toString();
            return str.length() >= 2 ? str.substring(0, str.length() - 2) : "";
        }
        return "";
    }

    @Override
    public String serialize(ValueRecipe value) {
        if (!value.getRawValue()
            .isPresent()) return "";
        return IRecipeDefinition.serialize(
            value.getRawValue()
                .get())
            .toString();
    }

    @Override
    public ValueRecipe deserialize(String value) {
        if (Strings.isNullOrEmpty(value)) return ValueRecipe.of(null);
        try {
            NBTTagCompound tag = (NBTTagCompound) JsonToNBT.func_150315_a(value);
            return ValueRecipe.of(IRecipeDefinition.deserialize(tag));
        } catch (NBTException e) {
            return ValueRecipe.of(null);
        }
    }

    @Override
    public String getName(ValueRecipe a) {
        return toCompactString(a);
    }

    @Override
    public boolean isNull(ValueRecipe a) {
        return !a.getRawValue()
            .isPresent();
    }

    @Override
    public ValueTypeLPElementBase createLogicProgrammerElement() {
        return new ValueTypeRecipeLPElement();
    }

    @ToString
    public static class ValueRecipe extends ValueOptionalBase<IRecipeDefinition> {

        private ValueRecipe(IRecipeDefinition recipe) {
            super(ValueTypes.OBJECT_RECIPE, recipe);
        }

        public static ValueRecipe of(IRecipeDefinition recipe) {
            return new ValueRecipe(recipe);
        }

        @Override
        protected boolean isEqual(IRecipeDefinition a, IRecipeDefinition b) {
            return a.equals(b);
        }
    }

}
