package ruiseki.integratedterminals.core.terminalstorage;

import java.util.Collection;
import java.util.Map;

import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Maps;

import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalStorageTabIngredientCraftingHandler;
import ruiseki.integratedterminals.api.terminalstorage.crafting.ITerminalStorageTabIngredientCraftingHandlerRegistry;

/**
 * Implementation of {@link ITerminalStorageTabIngredientCraftingHandlerRegistry}.
 * 
 * @author rubensworks
 */
public class TerminalStorageTabIngredientCraftingHandlerRegistry
    implements ITerminalStorageTabIngredientCraftingHandlerRegistry {

    private static TerminalStorageTabIngredientCraftingHandlerRegistry INSTANCE = new TerminalStorageTabIngredientCraftingHandlerRegistry();

    private final Map<ResourceLocation, ITerminalStorageTabIngredientCraftingHandler> handlers = Maps.newHashMap();

    private TerminalStorageTabIngredientCraftingHandlerRegistry() {

    }

    public static TerminalStorageTabIngredientCraftingHandlerRegistry getInstance() {
        return INSTANCE;
    }

    @Override
    public <T extends ITerminalStorageTabIngredientCraftingHandler> T register(T handler) {
        handlers.put(handler.getId(), handler);
        return handler;
    }

    @Override
    public Collection<ITerminalStorageTabIngredientCraftingHandler> getHandlers() {
        return handlers.values();
    }

    @Nullable
    @Override
    public ITerminalStorageTabIngredientCraftingHandler getHandler(ResourceLocation id) {
        return handlers.get(id);
    }
}
