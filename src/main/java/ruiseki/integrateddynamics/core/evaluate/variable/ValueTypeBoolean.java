package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.Constants;

import lombok.ToString;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeBooleanLPElement;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeLPElementBase;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Value type with values 'true' or 'false'
 *
 * @author rubensworks
 */
public class ValueTypeBoolean extends ValueTypeBase<ValueTypeBoolean.ValueBoolean> {

    private static ValueBoolean TRUE;
    private static ValueBoolean FALSE;

    public ValueTypeBoolean() {
        super(
            "boolean",
            Helpers.RGBToInt(43, 47, 231),
            EnumChatFormatting.BLUE.toString(),
            ValueTypeBoolean.ValueBoolean.class);
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
    public NBTBase serialize(ValueBoolean value) {
        return new NBTTagByte((byte) (value.getRawValue() ? 1 : 0));
    }

    @Override
    public ValueBoolean deserialize(NBTBase value) {
        if (value.getId() == Constants.NBT.TAG_BYTE) {
            return ValueBoolean.of(((NBTTagByte) value).func_150290_f() == 1);
        } else {
            throw new IllegalArgumentException(String.format("Value \"%s\" could not be parsed to a boolean.", value));
        }
    }

    @Override
    public String toString(ValueBoolean value) {
        return Boolean.toString(value.getRawValue());
    }

    @Override
    public ValueBoolean parseString(String value) throws EvaluationException {
        boolean b;
        if ("true".equalsIgnoreCase(value) || "1".equals(value)) {
            b = true;
        } else if ("false".equalsIgnoreCase(value) || "0".equals(value)) {
            b = false;
        } else {
            throw new EvaluationException(
                LangHelpers
                    .localize(L10NValues.OPERATOR_ERROR_PARSE, value, LangHelpers.localize(getUnlocalizedName())));
        }
        return ValueBoolean.of(b);
    }

    @Override
    public ValueTypeLPElementBase createLogicProgrammerElement() {
        return new ValueTypeBooleanLPElement(this);
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

        @Override
        public int hashCode() {
            return getType().hashCode() + (value ? 1 : 0);
        }
    }

}
