package ruiseki.integrateddynamics.core.evaluate;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;

/**
 * Used for forwarding values to a next propagator.
 * 
 * @author rubensworks
 */
public interface IOperatorValuePropagator<I, O> {

    public O getOutput(I input) throws EvaluationException;

}
