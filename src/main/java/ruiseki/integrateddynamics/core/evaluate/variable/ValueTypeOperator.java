package ruiseki.integrateddynamics.core.evaluate.variable;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.util.EnumChatFormatting;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import lombok.ToString;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperator;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeNamed;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeUniquelyNamed;
import ruiseki.integrateddynamics.core.evaluate.operator.Operators;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeLPElementBase;
import ruiseki.integrateddynamics.core.logicprogrammer.ValueTypeOperatorLPElement;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Value type with operator values.
 *
 * @author rubensworks
 */
public class ValueTypeOperator extends ValueTypeBase<ValueTypeOperator.ValueOperator> implements
    IValueTypeNamed<ValueTypeOperator.ValueOperator>, IValueTypeUniquelyNamed<ValueTypeOperator.ValueOperator> {

    private static final String SIGNATURE_LINK = "->";

    public ValueTypeOperator() {
        super("operator", Helpers.RGBToInt(43, 231, 47), EnumChatFormatting.DARK_GREEN.toString());
    }

    @Override
    public ValueOperator getDefault() {
        return ValueOperator.of(Operators.GENERAL_IDENTITY);
    }

    @Override
    public String toCompactString(ValueOperator value) {
        return value.getRawValue()
            .getLocalizedNameFull();
    }

    @Override
    public String serialize(ValueOperator value) {
        return Operators.REGISTRY.serialize(value.getRawValue());
    }

    @Override
    public ValueOperator deserialize(String value) {
        IOperator operator;
        try {
            operator = Operators.REGISTRY.deserialize(value);
        } catch (EvaluationException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        if (operator != null) {
            return ValueOperator.of(operator);
        }
        throw new IllegalArgumentException(String.format("Value \"%s\" could not be parsed to an operator.", value));
    }

    @Override
    public void loadTooltip(List<String> lines, boolean appendOptionalInfo, @Nullable ValueOperator value) {
        super.loadTooltip(lines, appendOptionalInfo, value);
        if (value != null) {
            lines.add(
                LangHelpers
                    .localize(L10NValues.VALUETYPEOPERATOR_TOOLTIP_SIGNATURE, getSignature(value.getRawValue())));
        }
    }

    @Override
    public ValueTypeLPElementBase createLogicProgrammerElement() {
        return new ValueTypeOperatorLPElement();
    }

    @Override
    public ValueOperator materialize(ValueOperator value) throws EvaluationException {
        return ValueOperator.of(
            value.getRawValue()
                .materialize());
    }

    /**
     * Pretty formatted signature of an operator.
     *
     * @param operator The operator.
     * @return The signature.
     */
    public static String getSignature(IOperator operator) {
        return StringUtils.join(getSignatureLines(operator, false), " ");
    }

    /**
     * Pretty formatted signature of an operator.
     *
     * @param inputTypes The input types.
     * @param outputType The output types.
     * @return The signature.
     */
    public static String getSignature(IValueType[] inputTypes, IValueType outputType) {
        return StringUtils.join(getSignatureLines(inputTypes, outputType, false), " ");
    }

    protected static StringBuilder switchSignatureLineContext(List<String> lines, StringBuilder sb) {
        lines.add(sb.toString());
        return new StringBuilder();
    }

    /**
     * Pretty formatted signature of an operator.
     *
     * @param inputTypes The input types.
     * @param outputType The output types.
     * @param indent     If the lines should be indented.
     * @return The signature.
     */
    public static List<String> getSignatureLines(IValueType[] inputTypes, IValueType outputType, boolean indent) {
        List<String> lines = Lists.newArrayList();

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (IValueType inputType : inputTypes) {
            if (first) {
                first = false;
            } else {
                sb = switchSignatureLineContext(lines, sb);
                sb.append((indent ? "  " : "") + SIGNATURE_LINK + " ");
            }
            sb.append(inputType.getDisplayColorFormat())
                .append(LangHelpers.localize(inputType.getUnlocalizedName()))
                .append(EnumChatFormatting.RESET);
        }
        sb.append(")");

        sb = switchSignatureLineContext(lines, sb);
        sb.append((indent ? "  " : "") + SIGNATURE_LINK + " ")
            .append(outputType.getDisplayColorFormat())
            .append(LangHelpers.localize(outputType.getUnlocalizedName()))
            .append(EnumChatFormatting.RESET);
        switchSignatureLineContext(lines, sb);
        return lines;
    }

    /**
     * Pretty formatted signature of an operator.
     *
     * @param operator The operator.
     * @param indent   If the lines should be indented.
     * @return The signature.
     */
    public static List<String> getSignatureLines(IOperator operator, boolean indent) {
        return getSignatureLines(operator.getInputTypes(), operator.getOutputType(), indent);
    }

    @Override
    public String getName(ValueTypeOperator.ValueOperator a) {
        return a.getRawValue()
            .getLocalizedNameFull();
    }

    @Override
    public String getUniqueName(ValueOperator a) {
        return a.getRawValue()
            .getUniqueName();
    }

    @ToString
    public static class ValueOperator extends ValueBase {

        private final IOperator value;

        private ValueOperator(IOperator value) {
            super(ValueTypes.OPERATOR);
            this.value = value;
        }

        public static ValueOperator of(IOperator value) {
            return new ValueOperator(value);
        }

        public IOperator getRawValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            return o == this || (o instanceof ValueOperator && value.equals(((ValueOperator) o).value));
        }

        @Override
        public int hashCode() {
            return 37 + value.hashCode();
        }
    }

}
