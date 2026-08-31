package ruiseki.integrateddynamics.core.evaluate.variable;

import java.util.Locale;

import net.minecraft.util.EnumChatFormatting;

import lombok.ToString;
import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNumber;
import ruiseki.okcore.helper.Helpers;

/**
 * Value type with values that are integers.
 * The raw value is nullable.
 *
 * @author rubensworks
 */
public class ValueTypeInteger extends ValueTypeBase<ValueTypeInteger.ValueInteger>
    implements IValueTypeNumber<ValueTypeInteger.ValueInteger> {

    public ValueTypeInteger() {
        super(
            "integer",
            Helpers.RGBToInt(243, 150, 4),
            EnumChatFormatting.GOLD.toString(),
            ValueTypeInteger.ValueInteger.class);
    }

    @Override
    public ValueInteger getDefault() {
        return ValueInteger.of(0);
    }

    @Override
    public String toCompactString(ValueInteger value) {
        return Integer.toString(value.getRawValue());
    }

    @Override
    public String serialize(ValueInteger value) {
        return Integer.toString(value.getRawValue());
    }

    @Override
    public ValueInteger deserialize(String value) {
        return ValueInteger.of(Integer.parseInt(value));
    }

    @Override
    public boolean isZero(ValueInteger a) {
        return a.getRawValue() == 0;
    }

    @Override
    public boolean isOne(ValueInteger a) {
        return a.getRawValue() == 1;
    }

    @Override
    public ValueInteger add(ValueInteger a, ValueInteger b) {
        return ValueInteger.of(a.getRawValue() + b.getRawValue());
    }

    @Override
    public ValueInteger subtract(ValueInteger a, ValueInteger b) {
        return ValueInteger.of(a.getRawValue() - b.getRawValue());
    }

    @Override
    public ValueInteger multiply(ValueInteger a, ValueInteger b) {
        return ValueInteger.of(a.getRawValue() * b.getRawValue());
    }

    @Override
    public ValueInteger divide(ValueInteger a, ValueInteger b) {
        return ValueInteger.of(a.getRawValue() / b.getRawValue());
    }

    @Override
    public ValueInteger max(ValueInteger a, ValueInteger b) {
        return ValueInteger.of(Math.max(a.getRawValue(), b.getRawValue()));
    }

    @Override
    public ValueInteger min(ValueInteger a, ValueInteger b) {
        return ValueInteger.of(Math.min(a.getRawValue(), b.getRawValue()));
    }

    @Override
    public ValueInteger increment(ValueInteger a) {
        return ValueInteger.of(a.getRawValue() + 1);
    }

    @Override
    public ValueInteger decrement(ValueInteger a) {
        return ValueInteger.of(a.getRawValue() - 1);
    }

    @Override
    public ValueInteger modulus(ValueInteger a, ValueInteger b) {
        return ValueInteger.of(a.getRawValue() % b.getRawValue());
    }

    @Override
    public boolean greaterThan(ValueInteger a, ValueInteger b) {
        return a.getRawValue() > b.getRawValue();
    }

    @Override
    public boolean lessThan(ValueInteger a, ValueInteger b) {
        return a.getRawValue() < b.getRawValue();
    }

    @Override
    public ValueInteger round(ValueInteger a) {
        return a;
    }

    @Override
    public ValueInteger ceil(ValueInteger a) {
        return a;
    }

    @Override
    public ValueInteger floor(ValueInteger a) {
        return a;
    }

    @Override
    public ValueTypeString.ValueString compact(ValueInteger a) {
        return ValueTypeString.ValueString.of(formatCompactInteger(a.getRawValue()));
    }

    private static String formatCompactInteger(int value) {
        if (value == Integer.MIN_VALUE) return formatCompactInteger(Integer.MIN_VALUE + 1);
        if (value < 0) return "-" + formatCompactInteger(-value);
        if (value < 1000) return Integer.toString(value);

        final String[] suffixesShort = new String[] { "", "K", "M", "B" };
        final String[] suffixesLong = new String[] { "", " thousand", " million", " billion" };

        String[] suffixes = GeneralConfig.numberCompactUseLongStyle ? suffixesLong : suffixesShort;

        int index = (int) (Math.log10(value) / 3);
        if (index >= suffixes.length) index = suffixes.length - 1;

        double num = value / Math.pow(10, index * 3);

        int maxDigits = GeneralConfig.numberCompactMaximumFractionDigits;
        if (maxDigits <= 0) {
            return String.format(Locale.US, "%.0f%s", num, suffixes[index]);
        }

        String pattern = "%." + maxDigits + "f%s";
        String formatted = String.format(Locale.US, pattern, num, suffixes[index]);

        return formatted.replaceAll("(\\.\\d*?[1-9])0+|\\.(0+)", "$1");
    }

    @Override
    public String getName(ValueInteger a) {
        return toCompactString(a);
    }

    @ToString
    public static class ValueInteger extends ValueBase {

        private final int value;

        private ValueInteger(int value) {
            super(ValueTypes.INTEGER);
            this.value = value;
        }

        public static ValueInteger of(int value) {
            return new ValueInteger(value);
        }

        public int getRawValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ValueInteger && ((ValueInteger) o).value == this.value;
        }

        @Override
        public int hashCode() {
            return getType().hashCode() + value;
        }
    }
}
