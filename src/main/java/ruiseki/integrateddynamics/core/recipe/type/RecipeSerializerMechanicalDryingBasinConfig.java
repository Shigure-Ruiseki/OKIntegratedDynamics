package ruiseki.integrateddynamics.core.recipe.type;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.RecipeConfig;

/**
 * Config for the mechanical drying basin recipe serializer.
 * 
 * @author rubensworks
 *
 */
public class RecipeSerializerMechanicalDryingBasinConfig extends RecipeConfig<RecipeMechanicalDryingBasin> {

    /**
     * The unique instance.
     */
    public static RecipeSerializerMechanicalDryingBasinConfig _instance;

    public RecipeSerializerMechanicalDryingBasinConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "mechanical_drying_basin",
            null,
            eConfig -> new RecipeSerializerMechanicalDryingBasin());
    }

}
