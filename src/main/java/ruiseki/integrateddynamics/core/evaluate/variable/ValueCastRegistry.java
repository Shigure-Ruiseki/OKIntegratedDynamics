package ruiseki.integrateddynamics.core.evaluate.variable;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Maps;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueCastRegistry;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.core.evaluate.operator.CastOperator;
import ruiseki.integrateddynamics.core.evaluate.operator.Operators;

/**
 * Registry for casting {@link IValue}.
 * 
 * @author rubensworks
 */
public final class ValueCastRegistry implements IValueCastRegistry {

    private static ValueCastRegistry INSTANCE = new ValueCastRegistry();

    private final Map<Pair<IValueType, IValueType>, IMapping> mappings = Maps.newHashMap();

    private ValueCastRegistry() {

    }

    /**
     * @return The unique instance.
     */
    public static ValueCastRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public <T1 extends IValueType<V1>, T2 extends IValueType<V2>, V1 extends IValue, V2 extends IValue> void register(
        T1 from, T2 to, IMapping<T1, T2, V1, V2> mapping) {
        mappings.put(Pair.<IValueType, IValueType>of(from, to), mapping);
        Operators.REGISTRY.register(new CastOperator<>(from, to, mapping));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T1 extends IValueType<V1>, T2 extends IValueType<V2>, V1 extends IValue, V2 extends IValue> V2 cast(
        T2 target, V1 value) throws ValueCastException {
        IMapping mapping = mappings.get(Pair.<IValueType, IValueType>of(value.getType(), target));
        if (mapping == null) {
            throw new ValueCastException(value.getType(), target);
        }
        return ((IMapping<T1, T2, V1, V2>) mapping).cast(value);
    }

    @Override
    public <T1 extends IValueType<V1>, T2 extends IValueType<V2>, V1 extends IValue, V2 extends IValue> boolean canCast(
        T2 target, V1 value) {
        return mappings.containsKey(Pair.<IValueType, IValueType>of(value.getType(), target));
    }
}
