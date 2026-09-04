package ruiseki.integrateddynamics.recipe;

import net.minecraft.item.ItemStack;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.core.part.PartTypes;
import ruiseki.integrateddynamics.part.PartTypeConnectorOmniDirectional;
import ruiseki.okcore.config.extendedconfig.RecipeConfig;
import ruiseki.okcore.recipe.type.RecipeCraftingShapelessCustomOutput;
import ruiseki.okcore.recipe.type.RecipeSerializerCraftingShapelessCustomOutput;

/**
 * @author rubensworks
 */
public class RecipeSerializerCraftingSpecialShapelessOmniDirectionalConfig
    extends RecipeConfig<RecipeCraftingShapelessCustomOutput> {

    /**
     * The unique instance.
     */
    public static RecipeSerializerCraftingSpecialShapelessOmniDirectionalConfig _instance;

    public RecipeSerializerCraftingSpecialShapelessOmniDirectionalConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "crafting_special_shapeless_omni_directional",
            null,
            eConfig -> new RecipeSerializerCraftingShapelessCustomOutput(
                () -> new ItemStack(PartTypes.CONNECTOR_OMNI.getItem(), 2),
                PartTypeConnectorOmniDirectional::transformCraftingOutput));
    }

}
