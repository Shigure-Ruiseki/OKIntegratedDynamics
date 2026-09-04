package ruiseki.integrateddynamics.capability.ingredient;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeLong;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.okcore.helper.LangHelpers;

/**
 * @author rubensworks
 */
public class IngredientComponentValueHandlerEnergy
    implements IIngredientComponentValueHandler<ValueTypeLong, ValueTypeLong.ValueLong, Long, Boolean> {

    private final IngredientComponent<Long, Boolean> ingredientComponent;

    public IngredientComponentValueHandlerEnergy(IngredientComponent<Long, Boolean> ingredientComponent) {
        this.ingredientComponent = ingredientComponent;
    }

    @Override
    public ValueTypeLong getValueType() {
        return ValueTypes.LONG;
    }

    @Override
    public IngredientComponent<Long, Boolean> getComponent() {
        return ingredientComponent;
    }

    @Override
    public ValueTypeLong.ValueLong toValue(@Nullable Long instance) {
        return ValueTypeLong.ValueLong.of(instance);
    }

    @Nullable
    @Override
    public Long toInstance(ValueTypeLong.ValueLong value) {
        return value.getRawValue();
    }

    @Override
    public String toCompactString(ValueTypeLong.ValueLong ingredientValue) {
        String value = getValueType().toCompactString(ingredientValue);
        value += " " + LangHelpers.localize(L10NValues.GENERAL_ENERGY_UNIT);
        return value;
    }

}
