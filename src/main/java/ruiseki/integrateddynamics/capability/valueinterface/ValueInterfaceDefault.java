package ruiseki.integrateddynamics.capability.valueinterface;

import java.util.Optional;

import ruiseki.integrateddynamics.api.evaluate.IValueInterface;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;

/**
 * Default implementation of {@link IValueInterface}.
 * 
 * @author rubensworks
 */
public class ValueInterfaceDefault implements IValueInterface {

    private IValue value;

    public ValueInterfaceDefault(IValue value) {
        this.value = value;
    }

    @Override
    public Optional<IValue> getValue() {
        return Optional.of(value);
    }

    public void setValue(IValue value) {
        this.value = value;
    }
}
