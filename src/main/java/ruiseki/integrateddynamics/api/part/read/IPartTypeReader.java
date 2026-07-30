package ruiseki.integrateddynamics.api.part.read;

import java.util.List;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspectRead;
import ruiseki.integrateddynamics.api.part.aspect.IAspectVariable;

/**
 * A part type for readers.
 * 
 * @author rubensworks
 */
public interface IPartTypeReader<P extends IPartTypeReader<P, S>, S extends IPartStateReader<P>>
    extends IPartType<P, S> {

    /**
     * @return All possible read aspects that can be used in this part type.
     */
    public List<IAspectRead> getReadAspects();

    /**
     * Get the singleton variable for an aspect.
     * 
     * @param target    The target block.
     * @param partState The state of this part.
     * @param aspect    The aspect from the part of this state.
     * @param <V>       The value type.
     * @param <T>       The value type type.
     * @return The variable that exists only once for an aspect in the given part state.
     */
    public <V extends IValue, T extends IValueType<V>> IAspectVariable<V> getVariable(PartTarget target, S partState,
        IAspectRead<V, T> aspect);

}
