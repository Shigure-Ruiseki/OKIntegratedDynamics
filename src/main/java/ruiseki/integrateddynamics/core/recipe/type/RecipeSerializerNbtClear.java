package ruiseki.integrateddynamics.core.recipe.type;

import java.io.IOException;

import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import ruiseki.okcore.helper.RecipeSerializerHelpers;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.ingredient.Ingredient;

public class RecipeSerializerNbtClear implements IRecipeSerializer<RecipeNbtClear> {

    @Override
    public RecipeNbtClear fromJson(ResourceLocation id, JsonObject json) {
        Ingredient inputIngredient = RecipeSerializerHelpers.getJsonIngredient(json, "item", false);
        return new RecipeNbtClear(id, inputIngredient);
    }

    @Override
    public @Nullable RecipeNbtClear fromNetwork(ResourceLocation id, ExtendedBuffer buffer) throws IOException {
        Ingredient inputIngredient = Ingredient.fromNetwork(buffer);
        return new RecipeNbtClear(id, inputIngredient);
    }

    @Override
    public void toNetwork(ExtendedBuffer buffer, RecipeNbtClear recipe) throws IOException {
        recipe.getInputIngredient()
            .toNetwork(buffer);
    }
}
