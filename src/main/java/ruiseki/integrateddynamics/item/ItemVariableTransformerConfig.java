package ruiseki.integrateddynamics.item;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.ItemConfig;
import ruiseki.okcore.item.ItemBase;

/**
 * Config for the Input and Output Variable Transformer.
 *
 * @author rubensworks
 *
 */
public class ItemVariableTransformerConfig extends ItemConfig {

    /**
     * The unique instance.
     */
    public static ItemVariableTransformerConfig _instance;

    /**
     * Make a new instance.
     */
    public ItemVariableTransformerConfig(boolean input) {
        super(
            IntegratedDynamics._instance,
            true,
            "variable_transformer_" + (input ? "input" : "output"),
            null,
            config -> new ItemBase());
    }

}
