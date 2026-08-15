package ruiseki.integrateddynamics.api.ingredient;

import java.util.Set;

import org.jetbrains.annotations.Nullable;

import ruiseki.commoncapabilities.api.ingredient.IngredientComponent;
import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integrateddynamics.api.evaluate.variable.IValueType;
import ruiseki.okcore.init.IRegistry;

/**
 * Registry for handling the translation between {@link IngredientComponent} instances and {@link IValue}.
 *
 * @author rubensworks
 */
public interface IIngredientComponentHandlerRegistry extends IRegistry {

    /**
     * Register a new recipe component handler.
     * 
     * @param handler The new handler.
     * @param <VT>    The value type.
     * @param <V>     The value.
     * @param <T>     The instance type.
     * @param <M>     The matching condition parameter.
     * 
     * @param <H>     The handler type.
     * @return The registered handler.
     */
    public <VT extends IValueType<V>, V extends IValue, T, M, H extends IIngredientComponentHandler<VT, V, T, M>> H register(
        H handler);

    /**
     * Get a handler by component type.
     * 
     * @param component The component type.
     * @param <VT>      The value type.
     * @param <V>       The value.
     * @param <T>       The instance type.
     * @param <M>       The matching condition parameter.
     * 
     * @return The handler or null.
     */
    @Nullable
    public <VT extends IValueType<V>, V extends IValue, T, M> IIngredientComponentHandler<VT, V, T, M> getComponentHandler(
        IngredientComponent<T, M> component);

    /**
     * @return The recipe components that have a handler.
     */
    public Set<IngredientComponent<?, ?>> getComponents();

}
