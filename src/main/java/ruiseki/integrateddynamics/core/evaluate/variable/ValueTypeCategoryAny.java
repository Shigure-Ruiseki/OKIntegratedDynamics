package ruiseki.integrateddynamics.core.evaluate.variable;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.okcore.helper.Helpers;

/**
 * Wildcard value type
 *
 * @author rubensworks
 */
public class ValueTypeCategoryAny extends ValueTypeCategoryBase<IValue> {

    public ValueTypeCategoryAny() {
        super("any", Helpers.RGBToInt(240, 240, 240), "", IValue.class);
    }

}
