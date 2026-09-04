package ruiseki.integrateddynamics.core.recipe.type;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.RecipeConfig;

/**
 * Config for the squeezer recipe serializer.
 *
 * @author rubensworks
 *
 */
public class RecipeSerializerMechanicalSqueezerConfig extends RecipeConfig<RecipeMechanicalSqueezer> {

    /**
     * The unique instance.
     */
    public static RecipeSerializerMechanicalSqueezerConfig _instance;

    public RecipeSerializerMechanicalSqueezerConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "mechanical_squeezer",
            null,
            eConfig -> new RecipeSerializerMechanicalSqueezer());
    }

}
