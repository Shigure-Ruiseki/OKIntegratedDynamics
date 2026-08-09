package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraft.util.EnumChatFormatting;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeUniquelyNamed;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.okcore.helper.Helpers;

/**
 * Value type category with values that have a unique name.
 * 
 * @author rubensworks
 */
public class ValueTypeCategoryUniquelyNamed extends ValueTypeCategoryBase<IValue> {

    public ValueTypeCategoryUniquelyNamed() {
        super("uniquely_named", Helpers.RGBToInt(250, 10, 13), EnumChatFormatting.RED.toString());
    }

    public String getUniqueName(IVariable a) throws EvaluationException {
        return ((IValueTypeUniquelyNamed) a.getType()).getUniqueName(a.getValue());
    }

    @Override
    public boolean correspondsTo(IValueType<?> valueType) {
        return super.correspondsTo(valueType) && valueType instanceof IValueTypeUniquelyNamed;
    }
}
