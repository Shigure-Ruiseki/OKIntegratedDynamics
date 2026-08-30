package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraft.util.EnumChatFormatting;

import lombok.ToString;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNumber;
import ruiseki.okcore.helper.Helpers;

/**
 * Value type with values that are doubles.
 *
 * @author rubensworks
 */
public class ValueTypeDouble extends ValueTypeBase<ValueTypeDouble.ValueDouble>
    implements IValueTypeNumber<ValueTypeDouble.ValueDouble> {

    public ValueTypeDouble() {
        super(
            "double",
            Helpers.RGBToInt(235, 234, 23),
            EnumChatFormatting.YELLOW.toString(),
            ValueTypeDouble.ValueDouble.class);
    }

    @Override
    public ValueDouble getDefault() {
        return ValueDouble.of(0D);
    }

    @Override
    public String toCompactString(ValueDouble value) {
        return Double.toString(value.getRawValue());
    }

    @Override
    public String serialize(ValueDouble value) {
        return Double.toString(value.getRawValue());
    }

    @Override
    public ValueDouble deserialize(String value) {
        return ValueDouble.of(Double.parseDouble(value));
    }

    @Override
    public boolean isZero(ValueDouble a) {
        return a.getRawValue() == 0D;
    }

    @Override
    public boolean isOne(ValueDouble a) {
        return a.getRawValue() == 1D;
    }

    @Override
    public ValueDouble add(ValueDouble a, ValueDouble b) {
        return ValueDouble.of(a.getRawValue() + b.getRawValue());
    }

    @Override
    public ValueDouble subtract(ValueDouble a, ValueDouble b) {
        return ValueDouble.of(a.getRawValue() - b.getRawValue());
    }

    @Override
    public ValueDouble multiply(ValueDouble a, ValueDouble b) {
        return ValueDouble.of(a.getRawValue() * b.getRawValue());
    }

    @Override
    public ValueDouble divide(ValueDouble a, ValueDouble b) {
        return ValueDouble.of(a.getRawValue() / b.getRawValue());
    }

    @Override
    public ValueDouble max(ValueDouble a, ValueDouble b) {
        return ValueDouble.of(Math.max(a.getRawValue(), b.getRawValue()));
    }

    @Override
    public ValueDouble min(ValueDouble a, ValueDouble b) {
        return ValueDouble.of(Math.min(a.getRawValue(), b.getRawValue()));
    }

    @Override
    public ValueDouble increment(ValueDouble a) {
        return ValueDouble.of(a.getRawValue() + 1D);
    }

    @Override
    public ValueDouble decrement(ValueDouble a) {
        return ValueDouble.of(a.getRawValue() - 1D);
    }

    @Override
    public ValueDouble modulus(ValueDouble a, ValueDouble b) {
        return ValueDouble.of(a.getRawValue() % b.getRawValue());
    }

    @Override
    public boolean greaterThan(ValueDouble a, ValueDouble b) {
        return a.getRawValue() > b.getRawValue();
    }

    @Override
    public boolean lessThan(ValueDouble a, ValueDouble b) {
        return a.getRawValue() < b.getRawValue();
    }

    @Override
    public ValueTypeInteger.ValueInteger round(ValueDouble a) {
        return ValueTypeInteger.ValueInteger.of((int) Math.round(a.getRawValue()));
    }

    @Override
    public ValueTypeInteger.ValueInteger ceil(ValueDouble a) {
        return ValueTypeInteger.ValueInteger.of((int) Math.ceil(a.getRawValue()));
    }

    @Override
    public ValueTypeInteger.ValueInteger floor(ValueDouble a) {
        return ValueTypeInteger.ValueInteger.of((int) Math.floor(a.getRawValue()));
    }

    @Override
    public String getName(ValueDouble a) {
        return toCompactString(a);
    }

    @ToString
    public static class ValueDouble extends ValueBase {

        private final double value;

        private ValueDouble(double value) {
            super(ValueTypes.DOUBLE);
            this.value = value;
        }

        public static ValueDouble of(double value) {
            return new ValueDouble(value);
        }

        public double getRawValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ValueDouble && ((ValueDouble) o).value == this.value;
        }

        @Override
        public int hashCode() {
            return getType().hashCode() + ((int) value * 100);
        }
    }

}
