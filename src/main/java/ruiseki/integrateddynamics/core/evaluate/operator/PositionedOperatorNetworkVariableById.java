package ruiseki.integrateddynamics.core.evaluate.operator;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.logicprogrammer.IConfigRenderPattern;
import ruiseki.integrateddynamics.api.network.INetwork;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueTypes;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.integrateddynamics.core.helper.NetworkHelpers;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.LangHelpers;

/**
 * An operator that gets a variable by id.
 *
 * @author rubensworks
 */
public class PositionedOperatorNetworkVariableById extends PositionedOperator {

    public PositionedOperatorNetworkVariableById(DimPos pos, ForgeDirection side) {
        super(
            "variablebyid",
            "variablebyid",
            new IValueType[] { ValueTypes.INTEGER },
            ValueTypes.CATEGORY_ANY,
            new Function(),
            IConfigRenderPattern.PREFIX_1,
            pos,
            side);
        ((PositionedOperatorNetworkVariableById.Function) this.getFunction()).setOperator(this);
    }

    public PositionedOperatorNetworkVariableById() {
        this(null, null);
    }

    @Override
    protected String getUnlocalizedType() {
        return "virtual";
    }

    public static class Function implements IFunction {

        private PositionedOperatorNetworkVariableById operator;

        public void setOperator(PositionedOperatorNetworkVariableById operator) {
            this.operator = operator;
        }

        public PositionedOperatorNetworkVariableById getOperator() {
            return operator;
        }

        @Override
        public IValue evaluate(SafeVariablesGetter variables) throws EvaluationException {
            if (getOperator() == null || getOperator().getPos() == null) {
                EvaluationException exception = new EvaluationException(
                    LangHelpers.localize(L10NValues.GENERAL_ERROR_NONETWORK));
                exception.setRetryEvaluation(true);
                throw exception;
            }

            INetwork network = NetworkHelpers.getNetwork(PartPos.of(getOperator().getPos(), getOperator().getSide()))
                .getOrNull();
            IPartNetwork partNetwork = NetworkHelpers.getPartNetwork(network)
                .getOrNull();

            if (network != null && partNetwork != null) {
                int variableId = variables.getValue(0, ValueTypes.INTEGER)
                    .getRawValue();
                IVariableFacade variableFacade = partNetwork.getVariableFacade(variableId);
                if (variableFacade != null) {
                    return variableFacade.getVariable(network, partNetwork)
                        .getValue();
                }
                EvaluationException exception = new EvaluationException(
                    LangHelpers.localize(L10NValues.OPERATOR_ERROR_VARIABLENOTINNETWORK, variableId));
                exception.setRetryEvaluation(true);
                throw exception;
            }

            EvaluationException exception = new EvaluationException(
                LangHelpers.localize(L10NValues.GENERAL_ERROR_NONETWORK));
            exception.setRetryEvaluation(true);
            throw exception;
        }
    }
}
