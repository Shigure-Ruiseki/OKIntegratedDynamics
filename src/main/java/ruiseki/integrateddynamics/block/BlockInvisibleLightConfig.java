package ruiseki.integrateddynamics.block;

import net.minecraft.item.Item;

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
        super(IntegratedDynamics._instance, true, "invisible_light", null, BlockInvisibleLight.class);
    }

    @Override
    public Class<? extends Item> getItemBlockClass() {
        return null;
    }
}
