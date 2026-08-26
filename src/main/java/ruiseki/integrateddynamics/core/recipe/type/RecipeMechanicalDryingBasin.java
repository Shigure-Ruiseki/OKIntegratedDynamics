package ruiseki.integrateddynamics.core.recipe.type;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.okcore.recipe.IRecipeSerializer;
import ruiseki.okcore.recipe.IRecipeType;
import ruiseki.okcore.recipe.ingredient.Ingredient;

/**
 * Mechanical drying basin recipe
 *
 * @author rubensworks
 */
public class RecipeMechanicalDryingBasin extends RecipeDryingBasin {

    public RecipeMechanicalDryingBasin(ResourceLocation id, Ingredient inputIngredient, FluidStack inputFluid,
        ItemStack outputItem, FluidStack outputFluid, int duration) {
        super(id, inputIngredient, inputFluid, outputItem, outputFluid, duration);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializerMechanicalDryingBasinConfig._instance.getInstance();
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeTypeMechanicalDryingBasinConfig._instance.getInstance();
    }
}
