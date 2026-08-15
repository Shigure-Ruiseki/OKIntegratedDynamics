package ruiseki.integrateddynamics.core.network;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import ruiseki.integrateddynamics.api.network.INetworkCraftingHandler;
import ruiseki.integrateddynamics.api.network.INetworkCraftingHandlerRegistry;

/**
 * Registry for {@link INetworkCraftingHandler}.
 * 
 * @author rubensworks
 */
public final class NetworkCraftingHandlerRegistry implements INetworkCraftingHandlerRegistry {

    private static NetworkCraftingHandlerRegistry INSTANCE = new NetworkCraftingHandlerRegistry();

    private List<INetworkCraftingHandler> handlers = Lists.newArrayList();

    private NetworkCraftingHandlerRegistry() {

    }

    /**
     * @return The unique instance.
     */
    public static NetworkCraftingHandlerRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public <C extends INetworkCraftingHandler> C register(C craftingHandler) {
        handlers.add(craftingHandler);
        return craftingHandler;
    }

    @Override
    public Collection<INetworkCraftingHandler> getCraftingHandlers() {
        return handlers;
    }
}
