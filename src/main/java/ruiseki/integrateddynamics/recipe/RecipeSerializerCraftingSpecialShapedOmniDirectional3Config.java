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
public class RecipeSerializerCraftingSpecialShapedOmniDirectional3Config
    extends RecipeConfig<RecipeCraftingShapedCustomOutput> {

    /**
     * The unique instance.
     */
    public static RecipeSerializerCraftingSpecialShapedOmniDirectional3Config _instance;

    public RecipeSerializerCraftingSpecialShapedOmniDirectional3Config() {
        super(
            IntegratedDynamics._instance,
            true,
            "crafting_special_shaped_omni_directional_3",
            null,
            eConfig -> new RecipeSerializerCraftingShapedCustomOutput(
                () -> new ItemStack(PartTypes.CONNECTOR_OMNI.getItem(), 3),
                PartTypeConnectorOmniDirectional::transformCraftingOutput));
    }
}
