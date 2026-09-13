package ruiseki.integratedrest.json.handler;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;
import ruiseki.integratedrest.api.json.IReverseValueTypeJsonHandler;

/**
 * @author rubensworks
 */
public abstract class CheckedValueTypeJsonHandlerBase<V extends IValue> implements IReverseValueTypeJsonHandler<V> {

    public abstract V handleUnchecked(JsonElement jsonElement) throws IllegalStateException, ClassCastException;

    @Nullable
    @Override
    public V handle(JsonElement jsonElement) {
        try {
            return handleUnchecked(jsonElement);
        } catch (IllegalStateException | ClassCastException | NumberFormatException | UnsupportedOperationException e) {
            return null;
        }
    }
}
