package ruiseki.integratedterminals.api.terminalstorage.crafting;

import java.util.Collection;

import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import ruiseki.okcore.init.IRegistry;

/**
 * A registry for {@link ITerminalStorageTabIngredientCraftingHandler}.
 * 
 * @author rubensworks
 */
public interface ITerminalStorageTabIngredientCraftingHandlerRegistry extends IRegistry {

    /**
     * Register a new terminal storage tab ingredient crafting handler.
     * 
     * @param handler The terminal storage tab ingredient crafting handler.
     * @param <T>     The handler type.
     * @return The registered handler.
     */
    public <T extends ITerminalStorageTabIngredientCraftingHandler> T register(T handler);

    /**
     * @return All registered handlers.
     */
    public Collection<ITerminalStorageTabIngredientCraftingHandler> getHandlers();

    /**
     * Get the handler with the given id.
     * 
     * @param id A handler id.
     * @return The registered handler or null.
     */
    @Nullable
    public ITerminalStorageTabIngredientCraftingHandler getHandler(ResourceLocation id);

}
