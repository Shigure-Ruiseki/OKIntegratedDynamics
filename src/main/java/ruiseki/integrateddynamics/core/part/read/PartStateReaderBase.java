package ruiseki.integrateddynamics.core.part.read;

import java.util.IdentityHashMap;
import java.util.Map;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.part.aspect.IAspect;
import ruiseki.integrateddynamics.api.part.aspect.IAspectRead;
import ruiseki.integrateddynamics.api.part.aspect.IAspectVariable;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.integrateddynamics.api.part.read.IPartStateReader;
import ruiseki.integrateddynamics.api.part.read.IPartTypeReader;
import ruiseki.integrateddynamics.core.part.PartStateBase;

/**
 * A default implementation of the {@link IPartStateReader}.
 *
 * @author rubensworks
 */
public class PartStateReaderBase<P extends IPartTypeReader> extends PartStateBase<P> implements IPartStateReader<P> {

    private final Map<IAspect, IAspectVariable> aspectVariables = new IdentityHashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public <V extends IValue, T extends IValueType<V>> IAspectVariable<V> getVariable(IAspectRead<V, T> aspect) {
        return aspectVariables.get(aspect);
    }

    @Override
    public void setVariable(IAspect aspect, IAspectVariable variable) {
        aspectVariables.put(aspect, variable);
    }

    @Override
    public void resetVariables() {
        for (IAspectVariable variable : this.aspectVariables.values()) {
            variable.invalidate();
        }
    }

    @Override
    public void setAspectProperties(IAspect aspect, IAspectProperties properties) {
        super.setAspectProperties(aspect, properties);
        this.aspectVariables.remove(aspect);
    }
}
