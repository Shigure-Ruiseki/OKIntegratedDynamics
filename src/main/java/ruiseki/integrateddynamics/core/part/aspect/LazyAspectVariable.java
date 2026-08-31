package ruiseki.integrateddynamics.core.part.aspect;

import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import lombok.Getter;
import lombok.NonNull;
import ruiseki.integrateddynamics.api.evaluate.EvaluationException;
import ruiseki.integrateddynamics.api.evaluate.expression.VariableAdapter;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.part.IPartState;
import ruiseki.integrateddynamics.api.part.IPartType;
import ruiseki.integrateddynamics.api.part.PartPos;
import ruiseki.integrateddynamics.api.part.PartTarget;
import ruiseki.integrateddynamics.api.part.aspect.IAspectRead;
import ruiseki.integrateddynamics.api.part.aspect.IAspectVariable;
import ruiseki.integrateddynamics.api.part.aspect.property.IAspectProperties;
import ruiseki.integrateddynamics.core.helper.L10NValues;
import ruiseki.okcore.helper.LangHelpers;

/**
 * Variable for a specific aspect from a part that calculates its target value only maximum once per ticking interval.
 * No calculations will be done if the value of this variable is not called.
 *
 * @author rubensworks
 */
public abstract class LazyAspectVariable<V extends IValue> extends VariableAdapter<V> implements IAspectVariable<V> {

    @Getter
    private final IValueType<V> type;
    @Getter
    private final Supplier<PartTarget> targetSupplier;
    @Getter
    private final IAspectRead<V, ?> aspect;
    @NonNull
    private V value;
    private IAspectProperties cachedProperties = null;

    private boolean isGettingValue = false;

    public LazyAspectVariable(IValueType<V> type, Supplier<PartTarget> targetSupplier, IAspectRead<V, ?> aspect) {
        this.type = type;
        this.targetSupplier = targetSupplier;
        this.aspect = aspect;
    }

    public PartTarget getTarget() {
        return targetSupplier.get();
    }

    @Override
    public void invalidate() {
        if (value != null) {
            value = null;
            cachedProperties = null;
        }
        super.invalidate();
    }

    @Override
    public V getValue() throws EvaluationException {
        if (value == null) {
            if (this.isGettingValue) {
                throw new EvaluationException(
                    new LangHelpers.UnlocalizedString(
                        L10NValues.VARIABLE_ERROR_RECURSION,
                        new LangHelpers.UnlocalizedString(getAspect().getUnlocalizedName())).localize());
            }
            this.isGettingValue = true;
            try {
                this.value = getValueLazy();
            } catch (EvaluationException e) {
                this.isGettingValue = false;
                throw e;
            }
            this.isGettingValue = false;
        }
        return this.value;
    }

    protected IAspectProperties getAspectProperties() {
        if (cachedProperties == null && getAspect().hasProperties()) {
            PartPos pos = getTarget().getCenter();
            Pair<IPartType, IPartState> partData = PartPos.getPartData(pos);
            if (partData != null) {
                cachedProperties = getAspect().getProperties(partData.getLeft(), getTarget(), partData.getRight());
            }
        }
        return cachedProperties;
    }

    /**
     * Calculate the current value for this variable.
     * It will only be called when required.
     *
     * @return The current value of this variable.
     * @throws EvaluationException If evaluation has gone wrong.
     */
    public abstract V getValueLazy() throws EvaluationException;

}
