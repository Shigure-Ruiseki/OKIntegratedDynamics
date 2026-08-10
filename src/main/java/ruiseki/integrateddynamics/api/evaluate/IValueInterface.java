package ruiseki.integrateddynamics.api.evaluate;

import java.util.Optional;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;

/**
 * A capability that can expose values.
 * 
 * @author rubensworks
 */
public interface IValueInterface {

    /**
     * Get a value.
     *
     * @return A value.
     * @throws EvaluationException If an error occurs while constructing or evaluating the value.
     */
    public Optional<IValue> getValue() throws EvaluationException;
}
