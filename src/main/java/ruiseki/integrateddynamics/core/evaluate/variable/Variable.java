package ruiseki.integrateddynamics.core.evaluate.variable;

import java.util.Objects;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.expression.VariableAdapter;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;

/**
 * A default variable implementation.
 *
 * @author rubensworks
 */
public class Variable<V extends IValue> extends VariableAdapter<V> {

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

    public Variable(V value) {
        this(value.getType(), value);
    }

    @Override
    public V getValue() throws EvaluationException {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Variable)) return false;
        Variable<?> variable = (Variable<?>) o;
        return Objects.equals(type, variable.type) && Objects.equals(value, variable.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }
}
