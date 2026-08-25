package ruiseki.integrateddynamics.modcompat.jjfmuy;

import ruiseki.integrateddynamics.client.gui.GuiLogicProgrammerBase;
import ruiseki.integrateddynamics.modcompat.jjfmuy.dryingbasin.DryingBasinRecipeCategory;
import ruiseki.integrateddynamics.modcompat.jjfmuy.squeezer.SqueezerRecipeCategory;
import ruiseki.jfmuy.api.IModPlugin;
import ruiseki.jfmuy.api.IModRegistry;
import ruiseki.jfmuy.api.JFMUYPlugin;
import ruiseki.jfmuy.api.recipe.IRecipeCategoryRegistration;

@JFMUYPlugin
public class JFMUYIDsConfig implements IModPlugin {

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        if (!JFMUYModCompat.canBeUsed) return;
        DryingBasinRecipeCategory.register(registry);
        SqueezerRecipeCategory.register(registry);
    }

    @Override
    public void register(IModRegistry registry) {
        if (!JFMUYModCompat.canBeUsed) return;
        registry.addGhostIngredientHandler(GuiLogicProgrammerBase.class, new LPGhostIngredientHandler<>());
        DryingBasinRecipeCategory.initialize(registry);
        SqueezerRecipeCategory.initialize(registry);
    }
}
