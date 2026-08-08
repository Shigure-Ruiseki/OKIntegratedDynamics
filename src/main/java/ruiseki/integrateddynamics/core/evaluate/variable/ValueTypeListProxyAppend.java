package ruiseki.integrateddynamics.core.evaluate.variable;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueTypeListProxy;

/**
 * An appended list.
 * 
 * @param <T> The value type type.
 * @param <V> The value type.
 */
public class ValueTypeListProxyAppend<T extends IValueType<V>, V extends IValue> extends ValueTypeListProxyBase<T, V> {

    private final IValueTypeListProxy<T, V> list;
    private final V value;

    public ValueTypeListProxyAppend(IValueTypeListProxy<T, V> list, V value) {
        super(list.getName(), list.getValueType());
        this.list = list;
        this.value = value;
    }

    @Override
    public int getLength() throws EvaluationException {
        return list.getLength() + 1;
    }

    @Override
    public V get(int index) throws EvaluationException {
        int listLength = list.getLength();
        if (index < listLength) {
            return list.get(index);
        } else if (index == listLength) {
            return value;
        }
        return null;
    }
}
