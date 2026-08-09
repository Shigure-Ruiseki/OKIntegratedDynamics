package ruiseki.integrateddynamics.block;

import net.minecraft.item.ItemBlock;

import ruiseki.integrateddynamics.IntegratedDynamics;
import ruiseki.integrateddynamics.item.ItemBlockProxy;
import ruiseki.okcore.config.extendedconfig.BlockContainerConfig;

/**
 * Config for {@link BlockProxy}.
 * 
 * @author rubensworks
 */
public class BlockProxyConfig extends BlockContainerConfig {

    /**
     * The unique instance.
     */
    public static BlockProxyConfig _instance;

    /**
     * Make a new instance.
     */
    public BlockProxyConfig() {
        super(IntegratedDynamics._instance, true, "proxy", null, BlockProxy.class);
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockProxy.class;
    }
}
