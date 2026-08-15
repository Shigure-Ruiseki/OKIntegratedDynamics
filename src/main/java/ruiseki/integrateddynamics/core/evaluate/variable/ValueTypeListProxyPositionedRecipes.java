package ruiseki.integrateddynamics.core.evaluate.variable;

import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Iterables;

import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeDefinition;
import ruiseki.commoncapabilities.api.capability.recipehandler.IRecipeHandler;
import ruiseki.commoncapabilities.capability.recipehandler.RecipeHandlerConfig;
import ruiseki.okcore.datastructure.DimPos;
import ruiseki.okcore.helper.Helpers;
import ruiseki.okcore.persist.nbt.INBTProvider;

/**
 * A list proxy for the recipes of a recipe handler at a certain position.
 */
public class ValueTypeListProxyPositionedRecipes extends
    ValueTypeListProxyPositioned<ValueObjectTypeRecipe, ValueObjectTypeRecipe.ValueRecipe> implements INBTProvider {

    public ValueTypeListProxyPositionedRecipes(DimPos pos, ForgeDirection side) {
        super(ValueTypeListProxyFactories.POSITIONED_RECIPES.getName(), ValueTypes.OBJECT_RECIPE, pos, side);
    }

    public ValueTypeListProxyPositionedRecipes() {
        this(null, null);
    }

    protected IRecipeHandler getRecipeHandler() {
        return Helpers
            .getTileOrBlockCapability(
                getPos().getWorld(),
                getPos().getBlockPos(),
                RecipeHandlerConfig.CAPABILITY,
                getSide())
            .getOrNull();
    }

    @Override
    public int getLength() {
        IRecipeHandler recipeHandler = getRecipeHandler();
        if (recipeHandler == null) {
            return 0;
        }
        return recipeHandler.getRecipes()
            .size();
    }

    @Override
    public ValueObjectTypeRecipe.ValueRecipe get(int index) {
        IRecipeDefinition recipeDefinition = Iterables.get(getRecipeHandler().getRecipes(), index);
        return ValueObjectTypeRecipe.ValueRecipe.of(recipeDefinition);
    }
}
