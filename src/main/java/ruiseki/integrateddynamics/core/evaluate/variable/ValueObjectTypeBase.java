package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraft.util.EnumChatFormatting;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.okcore.helper.Helpers;

/**
 * Base implementation of a value object type.
 *
 * @author rubensworks
 */
public abstract class ValueObjectTypeBase<V extends IValue> extends ValueTypeBase<V> {

    public ValueObjectTypeBase(String typeName, Class<V> valueClass) {
        this(typeName, Helpers.RGBToInt(243, 243, 243), EnumChatFormatting.GRAY.toString(), valueClass);
    }

    public ValueObjectTypeBase(String typeName, int color, String colorFormat, Class<V> valueClass) {
        super(typeName, color, colorFormat, valueClass);
    }

    @Override
    public boolean isObject() {
        return true;
    }
}
