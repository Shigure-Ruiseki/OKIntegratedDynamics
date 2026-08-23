package ruiseki.integrateddynamics.core.recipe.type;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.RecipeConfig;

/**
 * Config for {@link RecipeNbtClear}.
 * 
 * @author rubensworks
 */
public class RecipeSerializerNbtClearConfig extends RecipeConfig<RecipeNbtClear> {

    /**
     * The unique instance.
     */
    public static RecipeSerializerNbtClearConfig _instance;

    public RecipeSerializerNbtClearConfig() {
        super(
            IntegratedDynamics._instance,
            true,
            "crafting_special_nbt_clear",
            null,
            eConfig -> new RecipeSerializerNbtClear());
    }

}
