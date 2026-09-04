package ruiseki.integrateddynamics.core.recipe.type;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.RecipeConfig;

/**
 * Config for the squeezer recipe serializer.
 * 
 * @author rubensworks
 *
 */
public class RecipeSerializerSqueezerConfig extends RecipeConfig<RecipeSqueezer> {

    /**
     * The unique instance.
     */
    public static RecipeSerializerSqueezerConfig _instance;

    public RecipeSerializerSqueezerConfig() {
        super(IntegratedDynamics._instance, true, "squeezer", null, eConfig -> new RecipeSerializerSqueezer());
    }

}
