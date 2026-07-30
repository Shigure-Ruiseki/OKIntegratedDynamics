package ruiseki.integrateddynamics.core.evaluate.variable;

import lombok.Data;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueCastRegistry;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;

/**
 * Base implementation of a variable.
 * 
 * @author rubensworks
 */
@Data
public abstract class ValueBase implements IValue {

    private final IValueType type;

    protected IValueCastRegistry getValueCastRegistry() {
        return ValueCastMappings.REGISTRY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V extends IValue> V cast(IValueType<V> valueType) throws IValueCastRegistry.ValueCastException {
        if (valueType == getType()) {
            return (V) this;
        } else {
            return getValueCastRegistry().cast(valueType, this);
        }
    }

    @Override
    public <V extends IValue> boolean canCast(IValueType<V> valueType) {
        return getValueCastRegistry().canCast(valueType, this);
    }
}
