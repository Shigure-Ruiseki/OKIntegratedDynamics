package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraft.util.EnumChatFormatting;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNamed;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.okcore.helper.Helpers;

/**
 * Value type category with values that have a name.
 * 
 * @author rubensworks
 */
public class ValueTypeCategoryNamed extends ValueTypeCategoryBase<IValue> {

    public ValueTypeCategoryNamed() {
        super("named", Helpers.RGBToInt(250, 10, 13), EnumChatFormatting.RED.toString());
    }

    public String getName(IVariable a) throws EvaluationException {
        return ((IValueTypeNamed) a.getType()).getName(a.getValue());
    }

    @Override
    public boolean correspondsTo(IValueType valueType) {
        return super.correspondsTo(valueType) && valueType instanceof IValueTypeNamed;
    }
}
