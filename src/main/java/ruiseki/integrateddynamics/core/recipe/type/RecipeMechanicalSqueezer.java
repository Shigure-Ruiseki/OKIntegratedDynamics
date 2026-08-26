package ruiseki.integrateddynamics.core.recipe.type;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.okcore.datastructure.NonNullList;
import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.ingredient.Ingredient;

/**
 * Mechanical squeezer recipe
 *
 * @author rubensworks
 */
public class RecipeMechanicalSqueezer extends RecipeSqueezer {

    private final int duration;

    public RecipeMechanicalSqueezer(ResourceLocation id, Ingredient inputIngredient,
        NonNullList<ItemStackChance> outputItems, FluidStack outputFluid, int duration) {
        super(id, inputIngredient, outputItems, outputFluid);
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializerMechanicalSqueezerConfig._instance.getInstance();
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeTypeMechanicalSqueezerConfig._instance.getInstance();
    }
}
