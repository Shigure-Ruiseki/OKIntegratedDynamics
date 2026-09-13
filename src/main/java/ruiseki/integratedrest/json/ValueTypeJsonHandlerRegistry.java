package ruiseki.integratedrest.json;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integratedrest.api.json.IReverseValueTypeJsonHandler;
import ruiseki.integratedrest.api.json.IValueTypeJsonHandler;
import ruiseki.integratedrest.api.json.IValueTypeJsonHandlerRegistry;

/**
 * Implementation of {@link IValueTypeJsonHandlerRegistry}.
 * 
 * @author rubensworks
 */
public class ValueTypeJsonHandlerRegistry implements IValueTypeJsonHandlerRegistry {

    private static ValueTypeJsonHandlerRegistry INSTANCE = new ValueTypeJsonHandlerRegistry();

    private ValueTypeJsonHandlerRegistry() {

    }

    public static ValueTypeJsonHandlerRegistry getInstance() {
        return INSTANCE;
    }

    private final Map<IValueType<?>, IValueTypeJsonHandler<?>> handlers = Maps.newIdentityHashMap();
    private final List<IReverseValueTypeJsonHandler<?>> reverseHandlers = Lists.newArrayList();

    @Override
    public <V extends IValue, T extends IValueType<V>> void registerHandler(T valueType,
        IValueTypeJsonHandler<V> handler) {
        handlers.put(valueType, handler);
    }

    @Override
    public <V extends IValue, T extends IValueType<V>> void registerReverseHandler(
        IReverseValueTypeJsonHandler<V> handler) {
        reverseHandlers.add(handler);
    }

    @Nullable
    @Override
    public <V extends IValue, T extends IValueType<V>> IValueTypeJsonHandler<V> getHandler(T valueType) {
        return (IValueTypeJsonHandler<V>) handlers.get(valueType);
    }

    @Nullable
    @Override
    public Collection<IReverseValueTypeJsonHandler<?>> getReverseHandlers() {
        return Collections.unmodifiableList(reverseHandlers);
    }
}
