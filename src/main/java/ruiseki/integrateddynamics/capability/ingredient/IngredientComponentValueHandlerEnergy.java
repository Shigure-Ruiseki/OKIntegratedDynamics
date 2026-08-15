package ruiseki.integrateddynamics.capability.ingredient;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeInteger;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.okcore.helper.LangHelpers;

/**
 * @author rubensworks
 */
public class IngredientComponentValueHandlerEnergy
    implements IIngredientComponentValueHandler<ValueTypeInteger, ValueTypeInteger.ValueInteger, Integer, Boolean> {

    private final IngredientComponent<Integer, Boolean> ingredientComponent;

    public IngredientComponentValueHandlerEnergy(IngredientComponent<Integer, Boolean> ingredientComponent) {
        this.ingredientComponent = ingredientComponent;
    }

    @Override
    public ValueTypeInteger getValueType() {
        return ValueTypes.INTEGER;
    }

    @Override
    public IngredientComponent<Integer, Boolean> getComponent() {
        return ingredientComponent;
    }

    @Override
    public ValueTypeInteger.ValueInteger toValue(@Nullable Integer instance) {
        return ValueTypeInteger.ValueInteger.of(instance);
    }

    @Nullable
    @Override
    public Integer toInstance(ValueTypeInteger.ValueInteger value) {
        return value.getRawValue();
    }

    @Override
    public String toCompactString(ValueTypeInteger.ValueInteger ingredientValue) {
        String value = getValueType().toCompactString(ingredientValue);
        value += " " + LangHelpers.localize(L10NValues.GENERAL_ENERGY_UNIT);
        return value;
    }

}
