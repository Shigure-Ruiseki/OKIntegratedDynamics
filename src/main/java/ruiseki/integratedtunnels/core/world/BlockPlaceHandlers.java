package ruiseki.integratedtunnels.core.world;

import ruiseki.integratedtunnels.IntegratedTunnels;
import ruiseki.integratedtunnels.api.world.IBlockPlaceHandlerRegistry;

/**
 * Collection of block place handlers
 * 
 * @author rubensworks
 */
public class BlockPlaceHandlers {

    public static final IBlockPlaceHandlerRegistry REGISTRY = IntegratedTunnels._instance.getRegistryManager()
        .getRegistry(IBlockPlaceHandlerRegistry.class);

    public static void load() {}

}
