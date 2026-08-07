package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraft.util.EnumChatFormatting;

import lombok.ToString;
import ruiseki.okcore.helper.Helpers;

/**
 * Value type with values 'true' or 'false'
 *
 * @author rubensworks
 */
public class ValueTypeBoolean extends ValueTypeBase<ValueTypeBoolean.ValueBoolean> {

    private static ValueBoolean TRUE;
    private static ValueBoolean FALSE;

    public ValueTypeBoolean() {
        super("boolean", Helpers.RGBToInt(43, 47, 231), EnumChatFormatting.BLUE.toString());
    }

    @Override
    public ValueBoolean getDefault() {
        return ValueBoolean.of(false);
    }

    @Override
    public String toCompactString(ValueBoolean value) {
        return Boolean.toString(value.getRawValue());
    }

    @Override
    public String serialize(ValueBoolean value) {
        return Boolean.toString(value.getRawValue());
    }

    @Override
    public ValueBoolean deserialize(String value) {
        boolean b;
        if ("true".equalsIgnoreCase(value) || "1".equals(value)) {
            b = true;
        } else if ("false".equalsIgnoreCase(value) || "0".equals(value)) {
            b = false;
        } else {
            throw new IllegalArgumentException(String.format("Value \"%s\" could not be parsed to a boolean.", value));
        }
        return ValueBoolean.of(b);
    }

    @ToString
    public static class ValueBoolean extends ValueBase {

        private final boolean value;

        private ValueBoolean(boolean value) {
            super(ValueTypes.BOOLEAN);
            this.value = value;
        }

        public static ValueBoolean of(boolean value) {
            if (value) {
                if (TRUE == null || TRUE.getType() == null) {
                    TRUE = new ValueBoolean(true);
                }
                return TRUE;
            } else {
                if (FALSE == null || FALSE.getType() == null) {
                    FALSE = new ValueBoolean(false);
                }
                return FALSE;
            }
        }

        public boolean getRawValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ValueBoolean && ((ValueBoolean) o).value == this.value;
        }
    }

}
