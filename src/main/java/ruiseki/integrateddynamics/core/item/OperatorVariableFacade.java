package ruiseki.integrateddynamics.core.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ruiseki.integrateddynamics.api.evaluate.expression.IExpression;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperator;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.item.IOperatorVariableFacade;
import ruiseki.integrateddynamics.api.item.IVariableFacade;
import ruiseki.integrateddynamics.api.network.IPartNetwork;
import ruiseki.integrateddynamics.core.evaluate.expression.LazyExpression;
import ruiseki.integrateddynamics.core.evaluate.variable.ValueHelpers;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.okcore.datastructure.Wrapper;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Variable facade for variables determined for operators based on other variables in the network determined by their
 * id.
 * 
 * @author rubensworks
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OperatorVariableFacade extends VariableFacadeBase implements IOperatorVariableFacade {

    private final IOperator operator;
    private final int[] variableIds;
    private IExpression expression = null;
    private int lastNetworkHash = -1;

    public OperatorVariableFacade(boolean generateId, IOperator operator, int[] variableIds) {
        super(generateId);
        this.operator = operator;
        this.variableIds = variableIds;
    }

    public OperatorVariableFacade(int id, IOperator operator, int[] variableIds) {
        super(id);
        this.operator = operator;
        this.variableIds = variableIds;
    }

    @Override
    public <V extends IValue> IVariable<V> getVariable(IPartNetwork network) {
        if (isValid()) {
            int newNetworkHash = network != null ? network.hashCode() : -1;
            if (expression == null || expression.hasErrored() || newNetworkHash != this.lastNetworkHash) {
                this.lastNetworkHash = newNetworkHash;
                IVariable[] variables = new IVariable[variableIds.length];
                for (int i = 0; i < variableIds.length; i++) {
                    int variableId = variableIds[i];
                    if (!network.hasVariableFacade(variableId)) {
                        return null;
                    }
                    IVariableFacade variableFacade = network.getVariableFacade(variableId);
                    if (!variableFacade.isValid() || variableFacade == this) {
                        return null;
                    }
                    variables[i] = variableFacade.getVariable(network);
                    if (variables[i] == this /* Cyclic reference */ || variables[i] == null) {
                        return null;
                    }
                }
                expression = new LazyExpression(getId(), operator, variables, network);
            }
            return expression;
        }
        return null;
    }

    @Override
    public boolean isValid() {
        return getVariableIds() != null && getOperator() != null;
    }

    @Override
    public void validate(IPartNetwork network, final IValidator validator, IValueType containingValueType) {
        if (!isValid()) {
            validator.addError(new LangHelpers.UnlocalizedString(L10NValues.VARIABLE_ERROR_INVALIDITEM));
        } else {
            IValueType[] valueTypes = new IValueType[variableIds.length];
            IVariable[] variables = new IVariable[variableIds.length];
            boolean checkFurther = true;
            for (int i = 0; i < variableIds.length; i++) {
                int variableId = variableIds[i];
                // Check valid id
                if (variableId < 0) {
                    validator.addError(new LangHelpers.UnlocalizedString(L10NValues.VARIABLE_ERROR_INVALIDITEM));
                    checkFurther = false;
                } else if (!network.hasVariableFacade(variableId)) { // Check id present in network
                    validator.addError(
                        new LangHelpers.UnlocalizedString(
                            L10NValues.OPERATOR_ERROR_VARIABLENOTINNETWORK,
                            Integer.toString(variableId)));
                    checkFurther = false;
                } else {
                    // Check variable represented by this id is valid.
                    IVariableFacade variableFacade = network.getVariableFacade(variableId);
                    if (variableFacade == this) {
                        validator.addError(
                            new LangHelpers.UnlocalizedString(
                                L10NValues.OPERATOR_ERROR_CYCLICREFERENCE,
                                Integer.toString(variableId)));
                        checkFurther = false;
                    } else if (variableFacade != null) {
                        IValueType valueType = getOperator().getInputTypes()[i];
                        final Wrapper<Boolean> isValid = new Wrapper<>(true);
                        variableFacade.validate(network, new IValidator() {

                            @Override
                            public void addError(LangHelpers.UnlocalizedString error) {
                                validator.addError(error);
                                isValid.set(false);
                            }
                        }, valueType);
                        if (isValid.get()) {
                            IVariable variable = variableFacade.getVariable(network);
                            if (variable != null) {
                                variables[i] = variable;
                                valueTypes[i] = variable.getType();
                            }
                        } else {
                            checkFurther = false;
                        }
                    }
                }
            }
            if (checkFurther) {
                // Check operator validity
                IOperator op = getOperator();
                LangHelpers.UnlocalizedString error = op.validateTypes(valueTypes);
                if (error != null) {
                    validator.addError(error);
                }
                // Check expected aspect type and operator output type
                IValueType outputType = op.getConditionalOutputType(variables);
                if (!ValueHelpers.correspondsTo(outputType, containingValueType)) {
                    validator.addError(
                        new LangHelpers.UnlocalizedString(
                            L10NValues.ASPECT_ERROR_INVALIDTYPE,
                            new LangHelpers.UnlocalizedString(containingValueType.getUnlocalizedName()),
                            new LangHelpers.UnlocalizedString(outputType.getUnlocalizedName())));
                }
            }
        }
    }

    @Override
    public IValueType getOutputType() {
        IOperator operator = getOperator();
        if (operator == null) return null;
        return operator.getOutputType();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(List<String> list, EntityPlayer entityPlayer) {
        if (isValid()) {
            getOperator().loadTooltip(list, false);
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            boolean first = true;
            for (int variableId : getVariableIds()) {
                if (!first) {
                    sb.append(",");
                }
                sb.append(getReferenceDisplay(variableId));
                first = false;
            }
            sb.append("}");
            list.add(LangHelpers.localize(L10NValues.OPERATOR_TOOLTIP_VARIABLEIDS, sb.toString()));
        }
        super.addInformation(list, entityPlayer);
    }

}
