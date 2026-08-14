package ruiseki.integratedcrafting.api.recipe;

import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeDefinition;

/**
 * A modifiable recipe index.
 * 
 * @author rubensworks
 */
public interface IRecipeIndexModifiable extends IRecipeIndex {

    /**
     * Add the given recipe to this index.
     * 
     * @param recipe A recipe.
     */
    public void addRecipe(IRecipeDefinition recipe);

    /**
     * Remove the given recipe from this index.
     * 
     * @param recipe A recipe.
     */
    public void removeRecipe(IRecipeDefinition recipe);

}
