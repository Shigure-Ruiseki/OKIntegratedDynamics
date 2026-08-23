package ruiseki.integrateddynamics.block;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.BlockContainerConfig;

/**
 * Config for {@link BlockMaterializer}.
 * 
 * @author rubensworks
 */
public class BlockMaterializerConfig extends BlockContainerConfig {

    /**
     * The unique instance.
     */
    public static BlockMaterializerConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockMaterializerConfig() {
        super(IntegratedDynamics._instance, true, "materializer", null, BlockMaterializer::new);
    }
}
