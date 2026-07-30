package ruiseki.integrateddynamics.core.evaluate.operator;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.okcore.helper.LangHelpers;

/**
 * A general identity operator.
 * 
 * @author rubensworks
 */
public class GeneralIdentityOperator extends GeneralOperator {

    public GeneralIdentityOperator(String symbol, String operatorName) {
        super(
            symbol,
            operatorName,
            new IValueType[] { ValueTypes.CATEGORY_ANY },
            ValueTypes.CATEGORY_ANY,
            new IFunction() {

                @Override
                public IValue evaluate(SafeVariablesGetter variables) throws EvaluationException {
                    return variables.getValue(0);
                }
            },
            IConfigRenderPattern.PREFIX_1);
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
        }
        return null;
    }

    @Override
    public IValueType getConditionalOutputType(IVariable[] input) {
        return input[0].getType();
    }

}
