package ruiseki.integratedrest.api.json;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;

import ruiseki.integrateddynamics.api.evaluate.variable.IValue;

/**
 * Handler for converting a value from JSON.
 * 
 * @author rubensworks
 */
public interface IReverseValueTypeJsonHandler<V extends IValue> {

    /**
     * Convert the given value from JSON.
     * 
     * @param jsonElement A JSON element.
     * @return A value, or null if it can't be handled.
     */
    @Nullable
    public V handle(JsonElement jsonElement);

}
