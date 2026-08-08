package ruiseki.integrateddynamics.core.evaluate.operator;

import java.util.Arrays;
import java.util.List;

import ruiseki.integrateddynamics.Reference;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperator;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.okcore.helper.LangHelpers;

/**
 * A basic abstract implementation of an operator.
 *
 * @author rubensworks
 */
public abstract class OperatorBase implements IOperator {

    private final String symbol;
    private final String operatorName;
    private final IValueType[] inputTypes;
    private final IValueType outputType;
    private final IFunction function;
    private final IConfigRenderPattern renderPattern;

    private String unlocalizedName = null;

    protected OperatorBase(String symbol, String operatorName, IValueType[] inputTypes, IValueType outputType,
        IFunction function, IConfigRenderPattern renderPattern) {
        this.symbol = symbol;
        this.operatorName = operatorName;
        this.inputTypes = inputTypes;
        this.outputType = outputType;
        this.function = function;
        this.renderPattern = renderPattern;
        if (renderPattern.getSlotPositions().length != inputTypes.length) {
            throw new IllegalArgumentException(
                String.format(
                    "The given config render pattern with %s slots is not "
                        + "compatible with the number of input types %s for %s",
                    renderPattern.getSlotPositions().length,
                    inputTypes.length,
                    symbol));
        }
    }

    public static IValueType[] constructInputVariables(int length, IValueType defaultType) {
        IValueType[] values = new IValueType[length];
        Arrays.fill(values, defaultType);
        return values;
    }

    protected abstract String getUnlocalizedType();

    protected IFunction getFunction() {
        return this.function;
    }

    @Override
    public String getUniqueName() {
        return getUnlocalizedName();
    }

    @Override
    public String getUnlocalizedName() {
        return unlocalizedName != null ? unlocalizedName : (unlocalizedName = getUnlocalizedPrefix() + ".name");
    }

    @Override
    public String getUnlocalizedCategoryName() {
        return getUnlocalizedCategoryPrefix() + ".name";
    }

    @Override
    public String getLocalizedNameFull() {
        return LangHelpers
            .localize(getUnlocalizedCategoryPrefix() + ".basename", LangHelpers.localize(getUnlocalizedName()));
    }

    protected String getUnlocalizedPrefix() {
        return "operator.operators." + getModId() + "." + getUnlocalizedType() + "." + getOperatorName();
    }

    protected String getUnlocalizedCategoryPrefix() {
        return "operator.operators." + getModId() + "." + getUnlocalizedType();
    }

    protected String getOperatorName() {
        return this.operatorName;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public void loadTooltip(List<String> lines, boolean appendOptionalInfo) {
        String operatorName = LangHelpers.localize(getUnlocalizedName());
        String categoryName = LangHelpers.localize(getUnlocalizedCategoryName());
        String symbol = getSymbol();
        String outputTypeName = LangHelpers.localize(getOutputType().getUnlocalizedName());
        lines.add(LangHelpers.localize(L10NValues.OPERATOR_TOOLTIP_OPERATORNAME, operatorName, symbol));
        lines.add(LangHelpers.localize(L10NValues.OPERATOR_TOOLTIP_OPERATORCATEGORY, categoryName));
        IValueType[] inputTypes = getInputTypes();
        for (int i = 0; i < inputTypes.length; i++) {
            lines.add(
                LangHelpers.localize(
                    L10NValues.OPERATOR_TOOLTIP_INPUTTYPENAME,
                    i + 1,
                    inputTypes[i].getDisplayColorFormat() + LangHelpers.localize(inputTypes[i].getUnlocalizedName())));
        }
        lines.add(
            LangHelpers.localize(
                L10NValues.OPERATOR_TOOLTIP_OUTPUTTYPENAME,
                getOutputType().getDisplayColorFormat() + outputTypeName));
        if (appendOptionalInfo) {
            LangHelpers.addOptionalInfo(lines, getUnlocalizedPrefix());
        }
    }

    @Override
    public IValueType[] getInputTypes() {
        return inputTypes;
    }

    @Override
    public IValueType getOutputType() {
        return outputType;
    }

    @Override
    public IValueType getConditionalOutputType(IVariable[] input) {
        return outputType;
    }

    @Override
    public IValue evaluate(IVariable[] input) throws EvaluationException {
        LangHelpers.UnlocalizedString error = validateTypes(ValueHelpers.from(input));
        if (error != null) {
            throw new EvaluationException(error.localize());
        }
        return function.evaluate(new SafeVariablesGetter(input));
    }

    @Override
    public int getRequiredInputLength() {
        return getInputTypes().length;
    }

    @Override
    public LangHelpers.UnlocalizedString validateTypes(IValueType[] input) {
        // Input size checking
        int requiredInputLength = getRequiredInputLength();
        if (input.length != requiredInputLength) {
            return new LangHelpers.UnlocalizedString(
                L10NValues.OPERATOR_ERROR_WRONGINPUTLENGTH,
                this.getOperatorName(),
                input.length,
                requiredInputLength);
        }
        // Input types checking
        for (int i = 0; i < requiredInputLength; i++) {
            IValueType inputType = input[i];
            if (inputType == null) {
                return new LangHelpers.UnlocalizedString(
                    L10NValues.OPERATOR_ERROR_NULLTYPE,
                    this.getOperatorName(),
                    Integer.toString(i));
            }
            if (!ValueHelpers.correspondsTo(getInputTypes()[i], inputType)) {
                return new LangHelpers.UnlocalizedString(
                    L10NValues.OPERATOR_ERROR_WRONGTYPE,
                    this.getOperatorName(),
                    new LangHelpers.UnlocalizedString(inputType.getUnlocalizedName()),
                    Integer.toString(i + 1),
                    new LangHelpers.UnlocalizedString(getInputTypes()[i].getUnlocalizedName()));
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "[Operator: " + getOperatorName() + "]";
    }

    protected String getModId() {
        return Reference.MOD_ID;
    }

    @Override
    public IConfigRenderPattern getRenderPattern() {
        return renderPattern;
    }

    @Override
    public IOperator materialize() throws EvaluationException {
        return this;
    }

    public static class SafeVariablesGetter {

        private final IVariable[] variables;

        public SafeVariablesGetter(IVariable... variables) {
            this.variables = variables;
        }

        public <V extends IValue> V getValue(int i) throws EvaluationException {
            return (V) variables[i].getValue();
        }

        public IVariable[] getVariables() {
            return this.variables;
        }

        public static class Shifted extends SafeVariablesGetter {

            public Shifted(int start, IVariable... variables) {
                super(Arrays.copyOfRange(variables, start, variables.length));
            }
        }
    }

    public static interface IFunction {

        /**
         * Evaluate this function for the given input.
         *
         * @param variables The input variables holder.
         * @return The output value.
         * @throws EvaluationException If an exception occurs while evaluating
         */
        public IValue evaluate(SafeVariablesGetter variables) throws EvaluationException;

    }

}
