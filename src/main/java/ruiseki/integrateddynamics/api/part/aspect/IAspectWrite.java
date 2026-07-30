package ruiseki.integrateddynamics.api.part.aspect;

import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.evaluate.variable.IVariable;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.write.IPartStateWriter;
import ruiseki.integrateddynamics.api.part.write.IPartTypeWriter;

/**
 * An element that can be used inside parts to access a specific aspect of something to write.
 *
 * @author rubensworks
 */
public interface IAspectWrite<V extends IValue, T extends IValueType<V>> extends IAspect<V, T> {

    /**
     * Write the given variable value for the given part.
     *
     * @param partType The part type.
     * @param target   The position that is targeted by the given part.
     * @param state    The current state of the given part.
     * @param variable The variable to write.
     * @param <P>      The part type type.
     * @param <S>      The part state.
     * @throws EvaluationException If something went wrong while evaluating the variable to write.
     */
    public <P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>> void write(P partType, PartTarget target,
        S state, IVariable<V> variable) throws EvaluationException;

    /**
     * When this aspect has become active.
     *
     * @param partType The part type.
     * @param target   The position that is targeted by the given part.
     * @param state    The current state of the given part.
     * @param <P>      The part type type.
     * @param <S>      The part state.
     */
    public <P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>> void onActivate(P partType,
        PartTarget target, S state);

    /**
     * When this aspect has become inactive.
     *
     * @param partType The part type.
     * @param target   The position that is targeted by the given part.
     * @param state    The current state of the given part.
     * @param <P>      The part type type.
     * @param <S>      The part state.
     */
    public <P extends IPartTypeWriter<P, S>, S extends IPartStateWriter<P>> void onDeactivate(P partType,
        PartTarget target, S state);

}
