package ruiseki.integrateddynamics.api.part.aspect;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.okcore.datastructure.DimPos;

/**
 * A variable that exists at a certain position.
 *
 * @author rubensworks
 */
public interface IPositionedVariable<V extends IValue> extends IVariable<V> {

    /**
     * @return The target of this aspect variable.
     */
    public DimPos getTarget();

    /**
     * @return If this variable requires updating.
     */
    public boolean requiresUpdate();

    /**
     * Called when this variable should update.
     * This is only called when required, so there is no guarantee that this is called in a regular pattern.
     */
    public void update();

}
