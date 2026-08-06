package ruiseki.integrateddynamics.core.evaluate.variable;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;

/**
 * A default variable implementation.
 *
 * @author rubensworks
 */
public class Variable<V extends IValue> implements IVariable<V> {

    private final IValueType<V> type;
    private final V value;

    public Variable(IValueType<V> type, V value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public IValueType<V> getType() {
        return type;
    }

    @Override
    public V getValue() throws EvaluationException {
        return value;
    }
}
