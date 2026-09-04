package ruiseki.integrateddynamics.core.evaluate.variable;

import java.util.Locale;

import net.minecraft.util.EnumChatFormatting;

import lombok.ToString;
import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNumber;
import ruiseki.okcore.helper.Helpers;

/**
 * Value type with values that are doubles.
 *
 * @author rubensworks
 */
public class ValueTypeLong extends ValueTypeBase<ValueTypeLong.ValueLong>
    implements IValueTypeNumber<ValueTypeLong.ValueLong> {

    public ValueTypeLong() {
        super(
            "long",
            Helpers.RGBToInt(215, 254, 23),
            EnumChatFormatting.YELLOW.toString(),
            ValueTypeLong.ValueLong.class);
    }

    @Override
    public ValueLong getDefault() {
        return ValueLong.of(0L);
    }

    @Override
    public String toCompactString(ValueLong value) {
        return Long.toString(value.getRawValue());
    }

    @Override
    public String serialize(ValueLong value) {
        return Long.toString(value.getRawValue());
    }

    @Override
    public ValueLong deserialize(String value) {
        return ValueLong.of(Long.parseLong(value));
    }

    @Override
    public boolean isZero(ValueLong a) {
        return a.getRawValue() == 0L;
    }

    @Override
    public boolean isOne(ValueLong a) {
        return a.getRawValue() == 1L;
    }

    @Override
    public ValueLong add(ValueLong a, ValueLong b) {
        return ValueLong.of(a.getRawValue() + b.getRawValue());
    }

    @Override
    public ValueLong subtract(ValueLong a, ValueLong b) {
        return ValueLong.of(a.getRawValue() - b.getRawValue());
    }

    @Override
    public ValueLong multiply(ValueLong a, ValueLong b) {
        return ValueLong.of(a.getRawValue() * b.getRawValue());
    }

    @Override
    public ValueLong divide(ValueLong a, ValueLong b) {
        return ValueLong.of(a.getRawValue() / b.getRawValue());
    }

    @Override
    public ValueLong max(ValueLong a, ValueLong b) {
        return ValueLong.of(Math.max(a.getRawValue(), b.getRawValue()));
    }

    @Override
    public ValueLong min(ValueLong a, ValueLong b) {
        return ValueLong.of(Math.min(a.getRawValue(), b.getRawValue()));
    }

    @Override
    public ValueLong increment(ValueLong a) {
        return ValueLong.of(a.getRawValue() + 1L);
    }

    @Override
    public ValueLong decrement(ValueLong a) {
        return ValueLong.of(a.getRawValue() - 1L);
    }

    @Override
    public ValueLong modulus(ValueLong a, ValueLong b) {
        return ValueLong.of(a.getRawValue() % b.getRawValue());
    }

    @Override
    public boolean greaterThan(ValueLong a, ValueLong b) {
        return a.getRawValue() > b.getRawValue();
    }

    @Override
    public boolean lessThan(ValueLong a, ValueLong b) {
        return a.getRawValue() < b.getRawValue();
    }

    @Override
    public ValueTypeInteger.ValueInteger round(ValueLong a) {
        return ValueTypeInteger.ValueInteger.of((int) a.getRawValue());
    }

    @Override
    public ValueTypeInteger.ValueInteger ceil(ValueLong a) {
        return ValueTypeInteger.ValueInteger.of((int) a.getRawValue());
    }

    @Override
    public ValueTypeInteger.ValueInteger floor(ValueLong a) {
        return ValueTypeInteger.ValueInteger.of((int) a.getRawValue());
    }

    @Override
    public ValueTypeString.ValueString compact(ValueLong a) {
        long value = a.getRawValue();
        return ValueTypeString.ValueString.of(formatCompactLong(value));
    }

    private static String formatCompactLong(long value) {
        if (value == Long.MIN_VALUE) return formatCompactLong(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + formatCompactLong(-value);
        if (value < 1000) return Long.toString(value);

        final String[] suffixes = new String[] { "", "K", "M", "B", "T", "P", "E" };
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
    public String getName(ValueLong a) {
        return toCompactString(a);
    }

    @ToString
    public static class ValueLong extends ValueBase {

        private final long value;

        private ValueLong(long value) {
            super(ValueTypes.LONG);
            this.value = value;
        }

        public static ValueLong of(long value) {
            return new ValueLong(value);
        }

        public long getRawValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ValueLong && ((ValueLong) o).value == this.value;
        }

        @Override
        public int hashCode() {
            return getType().hashCode() + (int) value;
        }
    }

}
