package ruiseki.integrateddynamics.api.evaluate.expression;

import java.util.List;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;

/**
 * A basic variable implementation.
 * 
 * @author rubensworks
 */
public abstract class VariableAdapter<V extends IValue> implements IVariable<V> {

    private List<IVariable<?>> dependents = Lists.newLinkedList();

    @Override
    public boolean canInvalidate() {
        return true;
    }

    @Override
    public void invalidate() {
        for (IVariable<?> dependent : dependents) {
            if (dependent.canInvalidate()) {
                dependent.invalidate();
            }
        }
        dependents.clear();
    }

    @Override
    public void addDependent(IVariable<?> dependent) {
        dependents.add(dependent);
    }
}
