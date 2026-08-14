package ruiseki.integratedcrafting.api.crafting;

import java.util.List;

import com.google.common.collect.Lists;

import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeDefinition;
import ruiseki.commoncapabilities.api.ingredient.IPrototypedIngredient;

/**
 * An exception for when an infinitely recursive crafting recipe was found.
 * 
 * @author rubensworks
 */
public class RecursiveCraftingRecipeException extends Exception {

    private final IPrototypedIngredient prototypedIngredient;
    private final List<IRecipeDefinition> recipeStack;

    public <T, M> RecursiveCraftingRecipeException(IPrototypedIngredient prototypedIngredient) {
        super();
        this.prototypedIngredient = prototypedIngredient;
        this.recipeStack = Lists.newArrayList();
    }

    public IPrototypedIngredient getPrototypedIngredient() {
        return prototypedIngredient;
    }

    public List<IRecipeDefinition> getRecipeStack() {
        return recipeStack;
    }

    @Override
    public String getMessage() {
        return String.format("Infinite recursive recipe detected: %s", getPrototypedIngredient());
    }

    @Override
    public String toString() {
        return String
            .format("[Infinite recursive recipe detected: %s, %s]", getPrototypedIngredient(), getRecipeStack());
    }

    public void addRecipe(IRecipeDefinition recipe) {
        this.recipeStack.add(recipe);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RecursiveCraftingRecipeException
            && ((RecursiveCraftingRecipeException) obj).getPrototypedIngredient()
                .equals(this.getPrototypedIngredient())
            && ((RecursiveCraftingRecipeException) obj).getRecipeStack()
                .equals(this.getRecipeStack());
    }
}
