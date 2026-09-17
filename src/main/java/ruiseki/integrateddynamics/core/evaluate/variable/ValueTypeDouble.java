package ruiseki.integrateddynamics.core.evaluate.variable;

import java.util.Locale;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.Constants;

import lombok.ToString;
import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNumber;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;

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
    public NBTBase serialize(ValueDouble value) {
        return new NBTTagDouble(value.getRawValue());
    }

    @Override
    public ValueDouble deserialize(NBTBase value) {
        if (value.getId() == Constants.NBT.TAG_DOUBLE) {
            return ValueDouble.of(((NBTTagDouble) value).func_150286_g());
        } else {
            throw new IllegalArgumentException(String.format("Value \"%s\" could not be parsed to a double.", value));
        }
    }

    @Override
    public String toString(ValueDouble value) {
        return Double.toString(value.getRawValue());
    }

    @Override
    public ValueDouble parseString(String value) throws EvaluationException {
        try {
            return ValueDouble.of(Double.parseDouble(value));
        } catch (NumberFormatException e) {
            throw new EvaluationException(
                LangHelpers
                    .localize(L10NValues.OPERATOR_ERROR_PARSE, value, LangHelpers.localize(getUnlocalizedName())));
        }
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
    public ValueTypeString.ValueString compact(ValueDouble a) {
        return ValueTypeString.ValueString.of(formatCompactDouble(a.getRawValue()));
    }

    private static String formatCompactDouble(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return Double.toString(value);
        }
        if (value < 0) {
            return "-" + formatCompactDouble(-value);
        }
        if (value < 1000) {
            return String.format(Locale.US, "%." + GeneralConfig.numberCompactMaximumFractionDigits + "f", value)
                .replaceAll("(\\.\\d*?[1-9])0+|\\.(0+)", "$1");
        }

        final String[] suffixesShort = new String[] { "", "K", "M", "B", "T", "P", "E" };
        final String[] suffixesLong = new String[] { "", " thousand", " million", " billion", " trillion",
            " quadrillion", " quintillion" };

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
