package ruiseki.integrateddynamics.block;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.okcore.config.extendedconfig.BlockConfig;

/**
 * Config for {@link BlockLogicProgrammer}.
 *
 * @author rubensworks
 */
public class BlockLogicProgrammerConfig extends BlockConfig {

    /**
     * The unique instance.
     */
    public static BlockLogicProgrammerConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockLogicProgrammerConfig() {
        super(IntegratedDynamics._instance, true, "logic_programmer", null, BlockLogicProgrammer.class);
    }

}
