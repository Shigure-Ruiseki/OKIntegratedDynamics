package ruiseki.integrateddynamics.core.recipe.type;

import java.io.IOException;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.helper.GsonHelpers;
import ruiseki.okcore.helper.RecipeSerializerHelpers;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.ingredient.Ingredient;

/**
 * Recipe serializer for squeezer recipes
 *
 * @author rubensworks
 */
public class RecipeSerializerSqueezer implements IRecipeSerializer<RecipeSqueezer> {

    protected static RecipeSqueezer.ItemStackChance getJsonItemStackChance(JsonObject json) {
        ItemStack itemStack = RecipeSerializerHelpers
            .getJsonItemStackOrTag(json, true, List.of(GeneralConfig.recipeTagOutputModPriorities));
        float chance = GsonHelpers.getAsFloat(json, "chance", 1.0F);
        return new RecipeSqueezer.ItemStackChance(itemStack, chance);
    }

    protected static NonNullList<RecipeSqueezer.ItemStackChance> getJsonItemStackChances(JsonObject json, String key) {
        JsonElement element = json.get(key);
        if (element == null) {
            return NonNullList.create();
        } else if (element.isJsonArray()) {
            JsonArray jsonElements = element.getAsJsonArray();
            NonNullList<RecipeSqueezer.ItemStackChance> elements = NonNullList.create();
            for (JsonElement jsonElement : jsonElements) {
                elements.add(getJsonItemStackChance(jsonElement.getAsJsonObject()));
            }
            return elements;
        } else {
            throw new JsonSyntaxException("A JSON array is required as value for " + key);
        }
    }

    @Override
    public RecipeSqueezer fromJson(ResourceLocation recipeId, JsonObject json) {
        JsonObject result = GsonHelpers.getAsJsonObject(json, "result");

        // Input
        Ingredient inputIngredient = RecipeSerializerHelpers.getJsonIngredient(json, "item", true);

        // Output
        NonNullList<RecipeSqueezer.ItemStackChance> outputItemStacks = getJsonItemStackChances(result, "items");
        FluidStack outputFluid = RecipeSerializerHelpers.getJsonFluidStack(result, "fluid", false);

        // Validation
        if (inputIngredient.isEmpty()) {
            throw new JsonSyntaxException("An input item is required");
        }
        if (outputItemStacks.isEmpty() && outputFluid == null) {
            throw new JsonSyntaxException("An output item or fluid is required");
        }

        return new RecipeSqueezer(recipeId, inputIngredient, outputItemStacks, outputFluid);
    }

    @Nullable
    @Override
    public RecipeSqueezer fromNetwork(ResourceLocation recipeId, ExtendedBuffer buffer) throws IOException {
        // Input
        Ingredient inputIngredient = Ingredient.fromNetwork(buffer);

        // Output Items
        NonNullList<RecipeSqueezer.ItemStackChance> outputItemStacks = NonNullList.create();
        int outputItemStacksCount = buffer.readInt();
        for (int i = 0; i < outputItemStacksCount; i++) {
            outputItemStacks
                .add(new RecipeSqueezer.ItemStackChance(buffer.readItemStackFromBuffer(), buffer.readFloat()));
        }

        FluidStack outputFluid = null;
        if (buffer.readBoolean()) {
            outputFluid = buffer.readFluidStack();
        }

        return new RecipeSqueezer(recipeId, inputIngredient, outputItemStacks, outputFluid);
    }

    @Override
    public void toNetwork(ExtendedBuffer buffer, RecipeSqueezer recipe) throws IOException {
        // Input
        recipe.getInputIngredient()
            .toNetwork(buffer);

        // Output Items
        buffer.writeInt(
            recipe.getOutputItems()
                .size());
        for (RecipeSqueezer.ItemStackChance outputItem : recipe.getOutputItems()) {
            buffer.writeItemStackToBuffer(outputItem.getItemStack());
            buffer.writeFloat(outputItem.getChance());
        }

        FluidStack outputFluid = recipe.getOutputFluid();
        if (outputFluid != null) {
            buffer.writeBoolean(true);
            buffer.writeFluidStack(outputFluid);
        } else {
            buffer.writeBoolean(false);
        }
    }
}
