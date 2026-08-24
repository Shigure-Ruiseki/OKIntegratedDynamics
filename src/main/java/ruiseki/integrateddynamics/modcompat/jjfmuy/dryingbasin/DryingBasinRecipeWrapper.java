package ruiseki.integrateddynamics.modcompat.jjfmuy.dryingbasin;

import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.integrateddynamics.core.recipe.type.RecipeDryingBasin;
import ruiseki.jfmuy.api.ingredients.IIngredients;
import ruiseki.jfmuy.api.ingredients.VanillaTypes;
import ruiseki.jfmuy.api.recipe.IRecipeWrapper;

public class DryingBasinRecipeWrapper implements IRecipeWrapper {

    private final RecipeDryingBasin recipe;

    public DryingBasinRecipeWrapper(RecipeDryingBasin recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        // 1. Input Items
        if (recipe.getInputIngredient() != null && !recipe.getInputIngredient()
            .isEmpty()) {
            List<ItemStack> matchingStacks = Arrays.asList(
                recipe.getInputIngredient()
                    .getItems());
            ingredients.setInputLists(VanillaTypes.ITEM, List.of(matchingStacks));
        }

        // 2. Input Fluid
        if (recipe.getInputFluid() != null) {
            ingredients.setInput(VanillaTypes.FLUID, recipe.getInputFluid());
        }

        // 3. Output Item
        if (recipe.getOutputItem() != null) {
            ingredients.setOutput(VanillaTypes.ITEM, recipe.getOutputItem());
        }

        // 4. Output Fluid
        if (recipe.getOutputFluid() != null) {
            ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutputFluid());
        }
    }

    public List<ItemStack> getInputItem() {
        if (recipe.getInputIngredient() != null && !recipe.getInputIngredient()
            .isEmpty()) {
            return Arrays.asList(
                recipe.getInputIngredient()
                    .getItems());
        }
        return List.of();
    }

    public ItemStack getOutputItem() {
        return recipe.getOutputItem();
    }

    public FluidStack getInputFluid() {
        return recipe.getInputFluid();
    }

    public FluidStack getOutputFluid() {
        return recipe.getOutputFluid();
    }

    public int getDuration() {
        return recipe.getDuration();
    }
}
