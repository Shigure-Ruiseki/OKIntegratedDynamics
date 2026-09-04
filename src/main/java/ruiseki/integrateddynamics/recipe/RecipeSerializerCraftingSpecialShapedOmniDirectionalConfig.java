package ruiseki.integrateddynamics.recipe;

import net.minecraft.item.ItemStack;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.core.part.PartTypes;
import ruiseki.integrateddynamics.part.PartTypeConnectorOmniDirectional;
import ruiseki.okcore.config.extendedconfig.RecipeConfig;
import ruiseki.okcore.recipe.type.RecipeCraftingShapedCustomOutput;
import ruiseki.okcore.recipe.type.RecipeSerializerCraftingShapedCustomOutput;

/**
 * @author rubensworks
 */
public class RecipeSerializerCraftingSpecialShapedOmniDirectionalConfig
    extends RecipeConfig<RecipeCraftingShapedCustomOutput> {

    /**
     * The unique instance.
     */
    public static RecipeSerializerCraftingSpecialShapedOmniDirectionalConfig _instance;

    public RecipeSerializerCraftingSpecialShapedOmniDirectionalConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "crafting_special_shaped_omni_directional",
            null,
            eConfig -> new RecipeSerializerCraftingShapedCustomOutput(
                () -> new ItemStack(PartTypes.CONNECTOR_OMNI.getItem(), 2),
                PartTypeConnectorOmniDirectional::transformCraftingOutput));
    }

}
