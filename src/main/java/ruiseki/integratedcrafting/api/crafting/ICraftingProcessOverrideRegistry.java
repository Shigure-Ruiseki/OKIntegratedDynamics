package ruiseki.integratedcrafting.api.crafting;

import java.util.Collection;

import ruiseki.okcore.init.IRegistry;

/**
 * A registry for {@link ICraftingProcessOverride}.
 * 
 * @author rubensworks
 */
public interface ICraftingProcessOverrideRegistry extends IRegistry {

    /**
     * Register a new crafting process override.
     * 
     * @param craftingProcessOverride The crafting process override to register.
     * @param <T>                     The tab type.
     * @return The registered crafting process override.
     */
    public <T extends ICraftingProcessOverride> T register(T craftingProcessOverride);

    /**
     * @return All registered tabs.
     */
    public Collection<ICraftingProcessOverride> getCraftingProcessOverrides();

}
