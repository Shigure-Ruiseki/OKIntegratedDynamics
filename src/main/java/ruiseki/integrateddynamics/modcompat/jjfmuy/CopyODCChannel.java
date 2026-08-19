package ruiseki.integrateddynamics.modcompat.jjfmuy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import ruiseki.jfmuy.api.ingredients.IIngredients;
import ruiseki.jfmuy.api.ingredients.VanillaTypes;
import ruiseki.jfmuy.api.recipe.wrapper.ICraftingRecipeWrapper;
import ruiseki.okcore.recipe.IRecipeOK;
import ruiseki.okcore.recipe.ingredient.Ingredient;

public class CopyODCChannel implements ICraftingRecipeWrapper {

    private final IRecipeOK recipe;

    public CopyODCChannel(IRecipeOK recipe) {
        this.recipe = recipe;
    }

    @Override
    public void getIngredients(@Nonnull IIngredients ingredients) {
        List<List<ItemStack>> collected = new ArrayList<>();

        for (Object obj : recipe.getIngredients()) {
            if (obj instanceof Ingredient ingredient) {
                List<ItemStack> options = Arrays.asList(ingredient.getItems());
                collected.add(options);
            }
        }

        ingredients.setInputLists(VanillaTypes.ITEM, collected);

        List<ItemStack> outputs = new ArrayList<>();
        outputs.add(recipe.getRecipeOutput());
        ingredients.setOutputs(VanillaTypes.ITEM, outputs);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return recipe.getId();
    }
}
