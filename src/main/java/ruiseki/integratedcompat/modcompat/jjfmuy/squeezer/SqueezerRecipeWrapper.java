package ruiseki.integratedcompat.modcompat.jjfmuy.squeezer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.integrateddynamics.core.recipe.type.RecipeSqueezer;
import ruiseki.jfmuy.api.ingredients.IIngredients;
import ruiseki.jfmuy.api.ingredients.VanillaTypes;
import ruiseki.jfmuy.api.recipe.IRecipeWrapper;

public class SqueezerRecipeWrapper implements IRecipeWrapper {

    private final RecipeSqueezer recipe;

    public SqueezerRecipeWrapper(RecipeSqueezer recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        if (recipe.getInputIngredient() != null && !recipe.getInputIngredient()
            .isEmpty()) {
            List<ItemStack> matchingStacks = Arrays.asList(
                recipe.getInputIngredient()
                    .getItems());
            ingredients.setInputLists(VanillaTypes.ITEM, List.of(matchingStacks));
        }

        if (recipe.getOutputItems() != null && !recipe.getOutputItems()
            .isEmpty()) {
            List<ItemStack> outputs = new ArrayList<>();
            for (RecipeSqueezer.ItemStackChance stackChance : recipe.getOutputItems()) {
                if (stackChance != null && stackChance.getItemStack() != null) {
                    outputs.add(stackChance.getItemStack());
                }
            }
            ingredients.setOutputs(VanillaTypes.ITEM, outputs);
        }

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

    public List<RecipeSqueezer.ItemStackChance> getOutputItems() {
        return recipe.getOutputItems();
    }

    public List<Float> getOutputChances() {
        List<Float> chances = new ArrayList<>();
        if (recipe.getOutputItems() != null) {
            for (RecipeSqueezer.ItemStackChance stackChance : recipe.getOutputItems()) {
                chances.add(stackChance.getChance());
            }
        }
        return chances;
    }

    public FluidStack getOutputFluid() {
        return recipe.getOutputFluid();
    }
}
