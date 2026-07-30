package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraft.util.EnumChatFormatting;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNullable;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.okcore.helper.Helpers;

/**
 * Value type category with values that can be null.
 * 
 * @author rubensworks
 */
public class ValueTypeCategoryNullable extends ValueTypeCategoryBase<IValue> {

    public ValueTypeCategoryNullable() {
        super("nullable", Helpers.RGBToInt(100, 100, 100), EnumChatFormatting.DARK_GRAY.toString());
    }

    public boolean isNull(IVariable a) throws EvaluationException {
        return ((IValueTypeNullable) a.getType()).isNull(a.getValue());
    }

    @Override
    public boolean correspondsTo(IValueType valueType) {
        return super.correspondsTo(valueType) && valueType instanceof IValueTypeNullable;
    }
}
