package ruiseki.integratedtunnels.core.world;

import ruiseki.integratedtunnels.IntegratedTunnels;
import ruiseki.integratedtunnels.api.world.IBlockBreakHandlerRegistry;

/**
 * Collection of block break handlers
 * 
 * @author rubensworks
 */
public class BlockBreakHandlers {

    public static final IBlockBreakHandlerRegistry REGISTRY = IntegratedTunnels._instance.getRegistryManager()
        .getRegistry(IBlockBreakHandlerRegistry.class);

    public static void load() {}

}
