package ruiseki.integratednbt.block;

import ruiseki.integratedrest.IntegratedRest;
import ruiseki.integratedrest.block.BlockHttp;
import ruiseki.okcore.config.extendedconfig.BlockContainerConfig;

/**
 * Config for {@link BlockHttp}.
 *
 * @author rubensworks
 */
public class BlockNbtExtractorConfig extends BlockContainerConfig {

    /**
     * The unique instance.
     */
    public static BlockNbtExtractorConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockNbtExtractorConfig() {
        super(IntegratedRest._instance, true, "nbt_extractor", null, BlockNbtExtractor::new);
    }
}
