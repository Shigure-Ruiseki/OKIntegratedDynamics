package ruiseki.integrateddynamics.api.part.aspect.property;

import java.util.Collection;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.okcore.persist.nbt.INBTSerializable;

/**
 * A property that can be used inside aspects.
 *
 * @author rubensworks
 */
public interface IAspectProperties extends INBTSerializable {

    /**
     * Use this with caution!
     * Better to use {@link ruiseki.integrateddynamics.api.part.aspect.IAspect#getPropertyTypes()} instead because this
     * object might hold deprecated elements.
     *
     * @return The types.
     */
    public Collection<IAspectPropertyTypeInstance> getTypes();

    /**
     * Get the value of the given type.
     *
     * @param type The type to get the value from.
     * @param <T>  The value type type.
     * @param <V>  The value type.
     * @return The value.
     */
    public <T extends IValueType<V>, V extends IValue> V getValue(IAspectPropertyTypeInstance<T, V> type);

    /**
     * Set the value for the given type.
     *
     * @param type  The type to get the value from.
     * @param <T>   The value type type.
     * @param <V>   The value type.
     * @param value The value.
     */
    public <T extends IValueType<V>, V extends IValue> void setValue(IAspectPropertyTypeInstance<T, V> type, V value);

    /**
     * @return A deep copy of the properties.
     */
    @SuppressWarnings({ "CloneDoesntCallSuperClone", "deprecation" })
    public IAspectProperties clone();
}
