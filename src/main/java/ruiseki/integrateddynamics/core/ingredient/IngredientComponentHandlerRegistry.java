package ruiseki.integrateddynamics.core.ingredient;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.integrateddynamics.api.ingredient.IIngredientComponentHandler;
import ruiseki.integrateddynamics.api.ingredient.IIngredientComponentHandlerRegistry;

/**
 * @author rubensworks
 */
public class IngredientComponentHandlerRegistry implements IIngredientComponentHandlerRegistry {

    private static IngredientComponentHandlerRegistry INSTANCE = new IngredientComponentHandlerRegistry();

    private final Map<IngredientComponent<?, ?>, IIngredientComponentHandler> componentTypes = Maps
        .newIdentityHashMap();

    private IngredientComponentHandlerRegistry() {

    }

    /**
     * @return The unique instance.
     */
    public static IngredientComponentHandlerRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public <VT extends IValueType<V>, V extends IValue, T, M, H extends IIngredientComponentHandler<VT, V, T, M>> H register(
        H handler) {
        this.componentTypes.put(
            Objects.requireNonNull(
                handler.getComponent(),
                "The recipe component of " + handler + " was null, it is probably not initialized yet!"),
            handler);
        return handler;
    }

    @Nullable
    @Override
    public <VT extends IValueType<V>, V extends IValue, T, M> IIngredientComponentHandler<VT, V, T, M> getComponentHandler(
        IngredientComponent<T, M> component) {
        return this.componentTypes.get(component);
    }

    @Override
    public Set<IngredientComponent<?, ?>> getComponents() {
        return this.componentTypes.keySet();
    }
}
