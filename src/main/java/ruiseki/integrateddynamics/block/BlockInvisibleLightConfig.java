package ruiseki.integrateddynamics.block;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.BlockConfig;

/**
 * Config for {@link BlockInvisibleLight}.
 * 
 * @author rubensworks
 */
public class BlockInvisibleLightConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockInvisibleLightConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockInvisibleLightConfig() {
        super(IntegratedDynamics._instance, true, "invisibleLight", null, BlockInvisibleLight.class);
    }

}
