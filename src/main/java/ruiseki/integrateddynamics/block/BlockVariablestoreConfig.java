package ruiseki.integrateddynamics.block;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.BlockContainerConfig;

/**
 * Config for {@link BlockVariablestore}.
 * 
 * @author rubensworks
 */
public class BlockVariablestoreConfig extends BlockContainerConfig {

    /**
     * The unique instance.
     */
    public static BlockVariablestoreConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockVariablestoreConfig() {
        super(IntegratedDynamics._instance, true, "variablestore", null, BlockVariablestore.class);
    }

}
