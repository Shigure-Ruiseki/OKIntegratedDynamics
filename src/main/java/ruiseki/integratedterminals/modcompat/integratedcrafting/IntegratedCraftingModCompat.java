package ruiseki.integratedterminals.modcompat.integratedcrafting;

import ruiseki.integratedcrafting.Reference;
import ruiseki.integratedterminals.core.terminalstorage.crafting.TerminalStorageTabIngredientCraftingHandlers;
import ruiseki.okcore.modcompat.IModCompat;

/**
 * @author rubensworks
 */
public class IntegratedCraftingModCompat implements IModCompat {

    @Override
    public void onInit(Step initStep) {
        if(initStep == Step.INIT) {
            TerminalStorageTabIngredientCraftingHandlers.REGISTRY.register(
                new TerminalStorageTabIngredientCraftingHandlerCraftingNetwork());
        }
    }

    @Override
    public String getModID() {
        return Reference.MOD_ID;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getComment() {
        return "Crafting Terminal and Storage Terminal crafting actions.";
    }

}
