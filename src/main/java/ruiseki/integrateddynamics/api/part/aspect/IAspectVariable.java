package ruiseki.integrateddynamics.api.part.aspect;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.part.PartTarget;

/**
 * A separate instance of this exists for each part.
 *
 * @author rubensworks
 */
public interface IAspectVariable<V extends IValue> extends IVariable<V> {

    /**
     * @return The target of this aspect variable.
     */
    public PartTarget getTarget();

    /**
     * @return The referenced aspect.
     */
    public IAspectRead<V, ?> getAspect();
}
