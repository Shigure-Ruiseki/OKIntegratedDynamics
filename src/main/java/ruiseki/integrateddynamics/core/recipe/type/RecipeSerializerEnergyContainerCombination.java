package ruiseki.integrateddynamics.core.recipe.type;

import java.io.IOException;

import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import ruiseki.okcore.helper.GsonHelpers;
import ruiseki.okcore.helper.RecipeSerializerHelpers;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.ingredient.Ingredient;

public class RecipeSerializerEnergyContainerCombination implements IRecipeSerializer<RecipeEnergyContainerCombination> {

    @Override
    public RecipeEnergyContainerCombination fromJson(ResourceLocation id, JsonObject json) {
        Ingredient inputIngredient = RecipeSerializerHelpers.getJsonIngredient(json, "item", false);
        int maxCapacity = GsonHelpers.getAsInt(json, "maxCapacity");
        return new RecipeEnergyContainerCombination(id, inputIngredient, maxCapacity);
    }

    @Override
    public @Nullable RecipeEnergyContainerCombination fromNetwork(ResourceLocation id, ExtendedBuffer buffer)
        throws IOException {
        Ingredient inputIngredient = Ingredient.fromNetwork(buffer);
        int maxCapacity = buffer.readInt();
        return new RecipeEnergyContainerCombination(id, inputIngredient, maxCapacity);
    }

    @Override
    public void toNetwork(ExtendedBuffer buffer, RecipeEnergyContainerCombination recipe) throws IOException {
        recipe.getBatteryItem()
            .toNetwork(buffer);
        buffer.writeInt(recipe.getMaxCapacity());
    }
}
