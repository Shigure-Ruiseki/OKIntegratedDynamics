package ruiseki.integrateddynamics.api.evaluate.expression;

import java.util.List;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariableInvalidateListener;

/**
 * A basic variable implementation.
 *
 * @author rubensworks
 */
public abstract class VariableAdapter<V extends IValue> implements IVariable<V> {

    private List<IVariableInvalidateListener> invalidateListeners = Lists.newLinkedList();

    @Override
    public void invalidate() {
        for (IVariableInvalidateListener invalidateListener : Lists.newArrayList(invalidateListeners)) {
            invalidateListener.invalidate();

        }
        invalidateListeners.clear();
    }

    @Override
    public void addInvalidationListener(IVariableInvalidateListener invalidateListener) {
        invalidateListeners.add(invalidateListener);
    }
}
