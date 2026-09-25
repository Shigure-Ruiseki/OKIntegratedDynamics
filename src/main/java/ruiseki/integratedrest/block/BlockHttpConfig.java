package ruiseki.integratedrest.block;

import ruiseki.integratedrest.IntegratedRest;
import ruiseki.okcore.config.extendedconfig.BlockContainerConfig;

/**
 * Config for {@link BlockHttp}.
 *
 * @author rubensworks
 */
public class BlockHttpConfig extends BlockContainerConfig {

    /**
     * The unique instance.
     */
    public static BlockHttpConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockHttpConfig() {
        super(IntegratedRest._instance, true, "http", null, blockConfig -> new BlockHttp());
    }
}
