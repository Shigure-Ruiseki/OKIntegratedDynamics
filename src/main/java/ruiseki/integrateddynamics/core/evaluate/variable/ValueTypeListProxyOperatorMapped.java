package ruiseki.integrateddynamics.core.evaluate.variable;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.operator.IOperator;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeListProxy;

/**
 * A list proxy for a list that is mapped to another list by an operator.
 */
public class ValueTypeListProxyOperatorMapped extends ValueTypeListProxyBase<IValueType<IValue>, IValue> {

    private final IOperator operator;
    private final IValueTypeListProxy listProxy;

    public ValueTypeListProxyOperatorMapped(IOperator operator, IValueTypeListProxy listProxy) {
        super(ValueTypeListProxyFactories.MATERIALIZED.getName(), operator.getOutputType());
        this.operator = operator;
        this.listProxy = listProxy;
    }

    @Override
    public int getLength() throws EvaluationException {
        return listProxy.getLength();
    }

    @Override
    public IValue get(int index) throws EvaluationException {
        IValue value = listProxy.get(index);
        return ValueHelpers.evaluateOperator(operator, value);
    }
}
