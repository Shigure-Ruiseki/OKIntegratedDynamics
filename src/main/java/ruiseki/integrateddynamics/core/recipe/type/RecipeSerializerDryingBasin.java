package ruiseki.integrateddynamics.core.recipe.type;

import java.io.IOException;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import ruiseki.integrateddynamics.GeneralConfig;
import ruiseki.okcore.helper.GsonHelpers;
import ruiseki.okcore.helper.RecipeSerializerHelpers;
import ruiseki.okcore.network.ExtendedBuffer;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.ingredient.Ingredient;

/**
 * Recipe serializer for drying basin recipes
 *
 * @author rubensworks
 */
public class RecipeSerializerDryingBasin implements IRecipeSerializer<RecipeDryingBasin> {

    @Override
    public RecipeDryingBasin fromJson(ResourceLocation recipeId, JsonObject json) {
        JsonObject result = GsonHelpers.getAsJsonObject(json, "result");

        // Input
        Ingredient inputIngredient = RecipeSerializerHelpers.getJsonIngredient(json, "item", false);
        FluidStack inputFluid = RecipeSerializerHelpers.getJsonFluidStack(json, "fluid", false);

        // Output
        ItemStack outputItemStack = RecipeSerializerHelpers
            .getJsonItemStackOrTag(result, false, List.of(GeneralConfig.recipeTagOutputModPriorities));
        FluidStack outputFluid = RecipeSerializerHelpers.getJsonFluidStack(result, "fluid", false);

        // Other stuff
        int duration = GsonHelpers.getAsInt(json, "duration");

        // Validation
        if (inputIngredient.isEmpty() && inputFluid == null) {
            throw new JsonSyntaxException("An input item or fluid is required");
        }
        if (outputItemStack == null && outputFluid == null) {
            throw new JsonSyntaxException("An output item or fluid is required");
        }
        if (inputFluid != null && outputFluid != null) {
            throw new JsonSyntaxException("Can't have both an input and output fluid");
        }
        if (duration <= 0) {
            throw new JsonSyntaxException("Durations must be higher than one tick");
        }

        return new RecipeDryingBasin(recipeId, inputIngredient, inputFluid, outputItemStack, outputFluid, duration);
    }

    @Nullable
    @Override
    public RecipeDryingBasin fromNetwork(ResourceLocation recipeId, ExtendedBuffer buffer) throws IOException {
        // Input
        Ingredient inputIngredient = Ingredient.fromNetwork(buffer);
        FluidStack inputFluid = null;
        if (buffer.readBoolean()) {
            inputFluid = buffer.readFluidStack();
        }

        // Output
        ItemStack outputItemStack = buffer.readItemStackFromBuffer();
        FluidStack outputFluid = null;
        if (buffer.readBoolean()) {
            outputFluid = buffer.readFluidStack();
        }

        // Other stuff
        int duration = buffer.readVarIntFromBuffer();

        return new RecipeDryingBasin(recipeId, inputIngredient, inputFluid, outputItemStack, outputFluid, duration);
    }

    @Override
    public void toNetwork(ExtendedBuffer buffer, RecipeDryingBasin recipe) throws IOException {
        // Input
        recipe.getInputIngredient()
            .toNetwork(buffer);

        if (recipe.getInputFluid() != null) {
            buffer.writeBoolean(true);
            buffer.writeFluidStack(recipe.getInputFluid());
        } else {
            buffer.writeBoolean(false);
        }

        // Output
        buffer.writeItemStackToBuffer(recipe.getOutputItem());

        if (recipe.getOutputFluid() != null) {
            buffer.writeBoolean(true);
            buffer.writeFluidStack(recipe.getOutputFluid());
        } else {
            buffer.writeBoolean(false);
        }

        // Other stuff
        buffer.writeVarIntToBuffer(recipe.getDuration());
    }
}
