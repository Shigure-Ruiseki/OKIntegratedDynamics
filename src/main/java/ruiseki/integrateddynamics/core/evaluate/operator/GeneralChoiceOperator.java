package ruiseki.integrateddynamics.core.evaluate.operator;

import org.apache.commons.lang3.tuple.Pair;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypeBoolean;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.okcore.helper.LangHelpers;

/**
 * A general choice operator.
 * 
 * @author rubensworks
 */
public class GeneralChoiceOperator extends GeneralOperator {

    public GeneralChoiceOperator(String symbol, String operatorName) {
        super(
            symbol,
            operatorName,
            new IValueType[] { ValueTypes.BOOLEAN, ValueTypes.CATEGORY_ANY, ValueTypes.CATEGORY_ANY },
            ValueTypes.CATEGORY_ANY,
            new IFunction() {

                @Override
                public IValue evaluate(SafeVariablesGetter variables) throws EvaluationException {
                    boolean a = ((ValueTypeBoolean.ValueBoolean) variables.getValue(0)).getRawValue();
                    return a ? variables.getValue(1) : variables.getValue(2);
                }
            },
            new IConfigRenderPattern.Base(
                100,
                22,
                new Pair[] { Pair.of(6, 2), Pair.of(60, 2), Pair.of(80, 2) },
                Pair.of(40, 2)));
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
        IValueType temporarySecondInputType = null;
        for (int i = 0; i < requiredInputLength; i++) {
            IValueType inputType = input[i];
            if (inputType == null) {
                return new LangHelpers.UnlocalizedString(
                    L10NValues.OPERATOR_ERROR_NULLTYPE,
                    this.getOperatorName(),
                    Integer.toString(i));
            }
            if (i == 0 && getInputTypes()[i] != inputType) {
                return new LangHelpers.UnlocalizedString(
                    L10NValues.OPERATOR_ERROR_WRONGTYPE,
                    this.getOperatorName(),
                    new LangHelpers.UnlocalizedString(inputType.getUnlocalizedName()),
                    Integer.toString(i),
                    new LangHelpers.UnlocalizedString(getInputTypes()[i].getUnlocalizedName()));
            } else if (i == 1) {
                temporarySecondInputType = inputType;
            } else if (i == 2) {
                if (!ValueHelpers.correspondsTo(temporarySecondInputType, inputType)) {
                    return new LangHelpers.UnlocalizedString(
                        L10NValues.OPERATOR_ERROR_WRONGTYPE,
                        this.getOperatorName(),
                        new LangHelpers.UnlocalizedString(inputType.getUnlocalizedName()),
                        Integer.toString(i),
                        new LangHelpers.UnlocalizedString(temporarySecondInputType.getUnlocalizedName()));
                }
            }
        }
        return null;
    }

    @Override
    public IValueType getConditionalOutputType(IVariable[] input) {
        return input[1].getType();
    }

}
