package ruiseki.integrateddynamics.core.recipe.type;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.RecipeConfig;

/**
 * Config for {@link RecipeEnergyContainerCombination}.
 * 
 * @author rubensworks
 */
public class RecipeEnergyContainerCombinationConfig extends RecipeConfig<RecipeEnergyContainerCombination> {

    /**
     * The unique instance.
     */
    public static RecipeEnergyContainerCombinationConfig _instance;

    public RecipeEnergyContainerCombinationConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "crafting_special_energycontainer_combination",
            null,
            eConfig -> new RecipeSerializerEnergyContainerCombination());
    }

}
